package com.ispring.gameplane.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.List;

/**
 * 敌机类，从上向下沿直线运动
 */
public abstract class EnemyPlane extends AutoSprite {

    private int power = 1;//敌机的抗打击能力
    private int value = 0;//打一个敌机的得分
    public int level = 0;//难度

    public int getHurt() {
        return hurt;
    }

    public void setHurt(int hurt) {
        this.hurt = hurt;
    }

    public int hurt = 0;//伤害

    public int getBulletCount() {
        return bulletCount;
    }

    public void setBulletCount(int bulletCount) {
        this.bulletCount = bulletCount+level;
    }

    protected int bulletCount;//子弹数量

    public EnemyPlane(Bitmap bitmap,int level){
        super(bitmap);
        this.level=level;
    }

    public void setPower(int power){
        this.power = (int) (power+level*power*0.5f);
    }

    public int getPower(){
        return power;
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public abstract void fight(GameView gameView);

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        super.beforeDraw(canvas, paint, gameView);
        if(bulletCount>0) {
            fight(gameView);
        }
    }

    @Override
    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
        super.afterDraw(canvas, paint, gameView);

        //绘制完成后要检查自身是否被子弹打中
        if(!isDestroyed()){
            //敌机在绘制完成后要判断是否被子弹打中

            List<Bullet> bullets = gameView.getAliveBullets();
            for(Bullet bullet : bullets){
                //判断敌机是否与子弹相交
                Point p = getCollidePointWithOther(bullet);
                if(p != null){
                    //如果有交点，说明子弹打到了飞机上
                    bullet.destroy();
                    power-=bullet.getHurt();
                    if(power <= 0){
                        //敌机已经没有能量了，执行爆炸效果
                        explode(gameView);
                        return;
                    }
                }
            }
        }
    }

    //创建爆炸效果后会销毁敌机
    public void explode(GameView gameView){
        //创建爆炸效果
        float centerX = getX() + getWidth() / 2;
        float centerY = getY() + getHeight() / 2;
        Bitmap bitmap = gameView.getExplosionBitmap();
        AnimSprite explosion = new AnimSprite(bitmap,14,1);
        explosion.setFrequency(2);
        explosion.centerTo(centerX, centerY);
        gameView.addSprite(explosion);

        //创建爆炸效果完成后，向GameView中添加得分并销毁敌机
        gameView.addScore(value);
        destroy();
    }
}