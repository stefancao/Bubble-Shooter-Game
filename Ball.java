package stefanjun.bubbleshootergame;

/**
 * Created by Jun Li on 4/14/2015.
 */
import android.graphics.Canvas;
import android.graphics.Paint;
import java.lang.Math;


import java.util.Random;
public class Ball {
	
	//global variables of object Ball
    public int set = 1;
    private int mR;	
    private float mX;
    private float mY;
    public int x_index;
    public int y_index;
    private int mColor;
    private int flag = 0;
    private int floating_flag = 0;
    private int move = 0;
    private float VelX = 0;
    private float VelY = 0;
    private final int VEL = 45;
    private BubbleShooterView bsv;


    public Ball(BubbleShooterView bsv, int x, int y, int color) {
        this.bsv = bsv;
        this.mX = x;
        this.mY = y;
        this.mR = bsv.getRadius();
        this.mColor = color;
        this.x_index = bsv.get_X_index(this.mX, this.mY);	//translates xy coordinate to xy index
        this.y_index = bsv.get_Y_index(this.mY);	//translates xy coordinate to xy index
    }

    public int getStates() {
        return this.move;
    }

    public void stopMove() {	//stop the shooting ball
        this.move = 0;
    }

    public int getColor() {
        return this.mColor;
    }

    public float getX() {
        return this.mX;
    }

    public float getY() {
        return this.mY;
    }

    public float getVelX() {
        return this.VelX;
    }

    public float getVelY() {
        return this.VelY;
    }

    public int getFlag() { return this.flag; }

    public void setFlag(int f) { this.flag = f; }

    public void draw(Canvas c) {	//draw the bubble/circle
        Paint paint = new Paint();
        paint.setColor(this.mColor);
        c.drawCircle(this.mX, this.mY, this.mR, paint);
    }

    public void SetVel(){	//determine VelX and VelY which is used to get the direction of the shooting ball
        double x = this.bsv.getClick_x() - this.bsv.getShooting_ball_x();
        double y = this.bsv.getClick_y() - this.bsv.getShooting_ball_y();
        if (y != 0) {
            this.move = 1;
        }

        if (this.move == 0) {
            this.VelX = this.VelY = 0;
        }
        else {
            this.VelX = (float)(this.VEL * x / (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))));
            this.VelY = (float)(this.VEL * y / (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))));
        }
    }

    public void stepCoordinates() {		//moving the shooting ball in steps (appears to be moving)
        int width = this.bsv.getWidth();
        int height = this.bsv.getHeight();

        mX += VelX;
        mY += VelY;
		
		//if hit ceiling, floor or borders, change the direction
        if ( mY > (height - mR) ) {
            VelY = -VelY;
            mY = height - mR;
        } else if (mY < mR) {
            VelY = 0;
            mY = mR;
        }

        if ( mX > (width - mR) ) {
            VelX = -VelX;
            mX = width - mR;
        } else if ( mX < mR) {
            VelX = -VelX;
            mX = mR;
        } else if (VelY == 0) {
            VelX = 0;
        }
    }
}