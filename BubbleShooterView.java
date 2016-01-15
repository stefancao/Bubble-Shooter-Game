package stefanjun.bubbleshootergame;

/**
 * Created by Jun Li on 4/14/2015.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;


// This is the ‘‘game engine ’’.
public class BubbleShooterView extends SurfaceView
        implements SurfaceHolder.Callback {

	//defining variables
    private int counter_lose = 0;
    private int counter_win = 0;
    
    private Ball shooting_ball = null;     //shooting ball on the bottom
    private Ball[][] ballArray;     		//ballArray storing all the bubbles
    private Ball[] explode = new Ball[100];		//array store balls to explode
    private BubbleShooterThread bst;
    private int num_balls_vertical;
    private int shooting_ball_x;
    private int shooting_ball_y;
    private int cell_length;
    private int radius;
    private int color;
    private int click_x = -1;      		//initialize to be negative until getting a user click
    private int click_y = -1;
    private int x_offset;		//xy offset to search for balls 6 directions (neighbours)
    private int y_offset;
    private int counts = 1;
    private int sub_count = 0;
    private int count_x;
    private int count_y;

	//array storing the 8 different colors
    private final int ColorArray[] = {Color.YELLOW, Color.BLACK, Color.GREEN, Color.RED, Color.BLUE,
            Color.GRAY, Color.CYAN, Color.MAGENTA};

    public double getClick_x() {
        return this.click_x;
    }

    public double getClick_y() {
        return this.click_y;
    }

    public int getShooting_ball_x() {
        return this.shooting_ball_x;
    }

    public int getShooting_ball_y() {
        return this.shooting_ball_y;
    }

    public int getRadius() {
        return this.radius;
    }

    public int get_X_offset() { return this.x_offset; }

    public int get_Y_offset() { return this.y_offset; }

    public int get_X_index(float mx, float my) {
        return ((int) mx - (get_Y_index(my) % 2) * cell_length) / (2 * cell_length);
    }

    public int get_Y_index(float my) {
        return (int) my / (2 * cell_length);
    }

    public int get_X_coordinate(int x_index, int y_index) {
        return 2 * cell_length * x_index + (1 + y_index % 2) * cell_length;
    }

    public int get_Y_coordinate(int y_index) {
        return 2 * cell_length * y_index + cell_length;
    }

    public float get_distance(float cur_x, float cur_y, float tar_x, float tar_y) {
        return (float) Math.sqrt(Math.pow(cur_x - tar_x, 2) + Math.pow(cur_y - tar_y, 2));
    }


    public BubbleShooterView(Context context) {
        super(context);
        // Notify the SurfaceHolder that you’d like to receive
        // SurfaceHolder callbacks.
        getHolder().addCallback(this);
        setFocusable(true);
        // Initialize game state variables. DON’T RENDER THE GAME YET.
        //...
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Construct game initial state (bubbles, etc.)
        //...
        // Launch animator thread.

        int width = getWidth();     //get width and height from the screen
        int height = getHeight();
        Random random = new Random();
        cell_length = (width / 10) / 2;
        radius = cell_length - 3;  //find radius, assuming can only have max 10 balls horizontally

        shooting_ball_x = width / 2;      //put x and y coordinate of shooting ball on bottom
        shooting_ball_y = height - (cell_length * 2);
        num_balls_vertical = (height / (cell_length * 2));       //how long vertical is


        //initialize the the game with bubbles half screen
        ballArray = new Ball[10][num_balls_vertical];

        for (int y = 0; y < num_balls_vertical / 2; y++) {
            for (int x = 0; x < 10; x++) {
                if ((y % 2 != 0) && (x == 9)) {
                    ballArray[x][y] = null;
                }
                else {
                    color = ColorArray[random.nextInt(ColorArray.length)];	//get a random color
                    ballArray[x][y] = new Ball(this, get_X_coordinate(x, y), get_Y_coordinate(y), color);
                }
            }
        }

        /*color = ColorArray[random.nextInt(ColorArray.length)];
        shooting_ball = new Ball(this, this.getShooting_ball_x(), this.getShooting_ball_y(), color);   //create the shooting ball
        */bst = new BubbleShooterThread(this);
        bst.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Respond to surface changes, e.g., aspect ratio changes.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // The cleanest way to stop a thread is by interrupting it.
        // BubbleShooterThread regularly checks its interrupt flag.
        bst.interrupt();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        renderGame(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // Update game state in response to events:
        // touch-down, touch-up, and touch-move.
        // Current finger position.



        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Update Game State.
                break;
            case MotionEvent.ACTION_MOVE:
                // Update Game State.
                break;
            case MotionEvent.ACTION_UP:
                // Update Game State.
                click_x = (int) e.getX();		//get x and y coordinate from user
                click_y = (int) e.getY();

                break;
        }
        return true;
    }

    public void advanceFrame(Canvas c) {
        // Update game state to animate moving or exploding bubbles
        // (e.g., advance location of moving bubble).
        //...
        renderGame(c);
    }

    private void renderGame(Canvas c) {
		//draw the white background
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        c.drawPaint(paint);
		
		//create the shooting ball
        if (shooting_ball == null) {
            Random random = new Random();
            color = ColorArray[random.nextInt(ColorArray.length)];	//get random color
            shooting_ball = new Ball(this, this.getShooting_ball_x(), this.getShooting_ball_y(), color);   //create the shooting ball
        }
        if (shooting_ball.getY() == cell_length) {

            shooting_ball = null;
        }
        //draw the bubble array
        for (int y = 0; y < num_balls_vertical; y++) {
            for (int x = 0; x < 10; x++) {
                if (ballArray[x][y] != null) {

                    ballArray[x][y].draw(c);
                }
            }
        }
		
        shooting_ball.draw(c);	//draw the shooting ball
        shoot_ball_by_click();

        if (shooting_ball.getStates() == 1) {		//if getstates = 1 -> move the ball
            shooting_ball.stepCoordinates();

            float post_x = shooting_ball.getX() + shooting_ball.getVelX();
            float post_y = shooting_ball.getY() + shooting_ball.getVelY();
            int post_x_index = get_X_index(post_x, post_y);
            int post_y_index = get_Y_index(post_y);
            int tar_x_index;
            int tar_y_index;
            int cur_x_index = get_X_index(shooting_ball.getX(), shooting_ball.getY());
            int cur_y_index = get_Y_index(shooting_ball.getY());
            float post_distance;

            if (post_y_index == 0 && ballArray[post_x_index][post_y_index] == null) {
                ballArray[post_x_index][post_y_index] = new Ball(this, get_X_coordinate(post_x_index, post_y_index), get_Y_coordinate(post_y_index), shooting_ball.getColor());
                shooting_ball = null;
                click_x = -1;
                click_y = -1;
                count_x = post_x_index;
                count_y = post_y_index;
                exploding(post_x_index, post_y_index);
                if (sub_count >= 6) {
                    exploded();
                    sub_count = 0;
                }
                checkFloating();
            }
            else {
                for (int direction = 1; direction < 7; direction++) {	//check the neighbours of the ball (6 directions)
                    offset_setting(post_y_index, direction);
                    tar_x_index = post_x_index + get_X_offset();
                    tar_y_index = post_y_index + get_Y_offset();
                    if (tar_x_index >= 0 && tar_x_index < -(tar_y_index % 2) + 10 &&
                            tar_y_index >= 0 && tar_y_index < num_balls_vertical)
                    {
                        if (ballArray[tar_x_index][tar_y_index] != null) {
                            post_distance = get_distance(post_x, post_y, get_X_coordinate(tar_x_index, tar_y_index), get_Y_coordinate(tar_y_index));
                            if (post_distance < 2 * radius) {
                                shooting_ball.stopMove();

                                int set_x, set_y;
                                if ((ballArray[post_x_index][post_y_index] == null) && !(post_y_index % 2 != 0 && post_x_index == 9)){
                                    set_x = post_x_index;
                                    set_y = post_y_index;
                                }
                                else {
                                    set_x = cur_x_index;
                                    set_y = cur_y_index;
                                }
                                ballArray[set_x][set_y] = new Ball(this, get_X_coordinate(set_x, set_y), get_Y_coordinate(set_y), shooting_ball.getColor());
                                shooting_ball = null;
                                click_x = -1;
                                click_y = -1;
                                count_x = set_x;
                                count_y = set_y;
                                exploding(set_x, set_y);
                                if (sub_count >= 6) {
                                    exploded();
                                    sub_count = 0;
                                }
                                checkFloating();
                            }
                        }

                    }

                }
            }

        }

		//check if not ball in ballarray left 
        for (int y = 0; y < num_balls_vertical; y++) {
            for (int x = 0; x < 10; x++) {
                if (ballArray[x][y] != null) {
                    counter_win++;
                }
            }
        }

		// check if the ball is in region game over
        for (int x = 0; x < 10; x++) {
            if (ballArray[x][num_balls_vertical-2] != null) {
                counter_lose++;
            }
        }

		//if game over, print out "you lose"
        if(counter_lose > 0) {
            Paint paint2 = new Paint();
            paint2.setColor(Color.GREEN);
            paint2.setTextSize(255);
            c.drawText("You Lose", 25, 950, paint2);
            bst.interrupt();
        }


        //if wins, print out "you win"
        if(counter_win == 0) {
            Paint paint2 = new Paint();
            paint2.setColor(Color.RED);
            paint2.setTextSize(270);
            c.drawText("You won", 25, 950, paint2);
            bst.interrupt();
        }
        counter_win = 0;

        // Render the game elements: bubbles (fixed ,moving ,exploding)
        // and aiming arrow.

    }
	
	//make sure that the user cannot click below the shooting ball and lower than 20 degrees
    private void shoot_ball_by_click() {

        double x_check = Math.abs(shooting_ball.getX() - click_x);
        double y_check = Math.abs(shooting_ball.getY() - click_y);
        double check = Math.toDegrees(Math.atan(y_check / x_check));
        if (check < 20.0 || click_y > shooting_ball.getY())
        {
            this.click_x = -1;
            this.click_y = -1;
        }

        if ((click_x > 0 && click_y > 0) && (shooting_ball.set != 0)){
            shooting_ball.SetVel();
            shooting_ball.set = 0;
        }
    }
	
	private void checkFloating() {
        int tar1x, tar1y;
        int tar2x, tar2y;
        for (int y = 0; y < num_balls_vertical; y++) {
            for (int x = 0; x < -(y % 2) + 10; x++) {
                if (y != 0){
                    offset_setting(y, 4);
                    tar1x = x + x_offset;
                    tar1y = y + y_offset;
                    offset_setting(y, 1);
                    tar2x = x + x_offset;
                    tar2y = y + y_offset;
                    if ((tar1x >= 0) && (tar1x < - (tar1y % 2) + 10) &&
                            (tar1y >= 0) && (tar1y < num_balls_vertical) &&
                            (tar2x >= 0) && (tar2x < - (tar2y % 2) + 10) &&
                            (tar2y >= 0) && (tar2y < num_balls_vertical)
                            ){
                        if (ballArray[tar1x][tar1y] == null && ballArray[tar2x][tar2y] == null) {
                            ballArray[x][y] = null;
                        }
                    }
                    else if (tar1x < 0){
                        if (ballArray[tar2x][tar2y] == null) {
                            ballArray[x][y] = null;
                        }
                    }
                    else if (tar2x >=  - (tar1y % 2) + 10) {
                        if (ballArray[tar1x][tar1y] == null) {
                            ballArray[x][y] = null;
                        }
                    }
                }
            }
        }
    }

//counts the number of balls in the same colour
    private void exploding(int x, int y) {
        int tar_x_index, tar_y_index;
        explode[counts] = ballArray[x][y];
        ballArray[x][y].setFlag(1);

        for (int direction = 1; direction < 7; direction++) {
            offset_setting(y, direction);
            tar_x_index = x + x_offset;
            tar_y_index = y + y_offset;

            System.out.println("(" + x + ", " + y+")"+"            ("+ tar_x_index + ", " + tar_y_index+ ")");
            if (x == count_x && y == count_y) {
                sub_count++;
                System.out.println(sub_count);
            }
            if ((tar_x_index >= 0) && (tar_x_index < - (tar_y_index % 2) + 10) &&
                    (tar_y_index >= 0) && (tar_y_index < num_balls_vertical))
            {
                if ((ballArray[x][y] != null) && (ballArray[tar_x_index][tar_y_index] != null))
                {
                    System.out.println(ballArray[x][y].getColor() + " ,  "+ ballArray[tar_x_index][tar_y_index].getColor());
                }

                if ((ballArray[tar_x_index][tar_y_index] != null) &&
                        (ballArray[tar_x_index][tar_y_index].getColor() == ballArray[x][y].getColor()) &&
                        (ballArray[tar_x_index][tar_y_index].getFlag() == 0)
                        )
                {

                    counts++;
                    System.out.println("COUNTS: " + counts);

                    exploding(tar_x_index, tar_y_index);
                }
            }

        }
    }
//explodes the same color balls if counts >= 3
    private void exploded() {
        System.out.println("EXPLODE!!!" + counts);
        int tar_x_index, tar_y_index;
        if (counts >= 3) {
            for (int i = 1; i <= counts; i++) {
                ballArray[get_X_index(explode[i].getX(), explode[i].getY())][get_Y_index(explode[i].getY())].setFlag(0);
                ballArray[get_X_index(explode[i].getX(), explode[i].getY())][get_Y_index(explode[i].getY())] = null;
                explode[i] = null;
            }

        }
        else {
            for (int i = 1; i <= counts; i++) {
                ballArray[get_X_index(explode[i].getX(), explode[i].getY())][get_Y_index(explode[i].getY())].setFlag(0);
                explode[counts].setFlag(0);
            }

        }
        counts = 1;
    }

	//determine the offset, 6 neighbours
    private void offset_setting(int y, int direction) {
        switch (direction) {
            case 0:
                x_offset = 0;
                y_offset = 0;
                break;
            case 1:
                x_offset = y % 2;
                y_offset = -1;
                break;
            case 2:
                x_offset = 1;
                y_offset = 0;
                break;
            case 3:
                x_offset = y % 2;
                y_offset = 1;
                break;
            case 4:
                x_offset = (y % 2) - 1;
                y_offset = -1;
                break;
            case 5:
                x_offset = -1;
                y_offset = 0;
                break;
            case 6:
                x_offset = (y % 2) - 1;
                y_offset = 1;
                break;
            default:
                break;
        }
    }
}
