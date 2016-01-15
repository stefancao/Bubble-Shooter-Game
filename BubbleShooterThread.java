package stefanjun.bubbleshootergame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by stefancao on 4/8/15.
 */

public class BubbleShooterThread extends Thread {
    BubbleShooterView bsv;

    public BubbleShooterThread(BubbleShooterView bsv) {
        this.bsv = bsv;
    }

    @Override
    public void run() {
        SurfaceHolder sh = bsv.getHolder();

        // Main game loop.
        while( !Thread.interrupted() ) {
            Canvas c = sh.lockCanvas(null);
            try {
                synchronized(sh) {
                    bsv.advanceFrame(c);
                }
            } catch (Exception e) {
            } finally {
                if ( c != null ) {
                    sh.unlockCanvasAndPost(c);
                }
            }
            // Set the frame rate by setting this delay
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                // Thread was interrupted while sleeping.
                return;
            }
        }
    }
}