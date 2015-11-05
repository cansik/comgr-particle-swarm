package ch.comgr.particleswarm.util;

/**
 * Created by cansik on 05/11/15.
 */
public class FPSCounter {
    volatile int frameCounter = 0;
    volatile long frameCounterStartTime;

    volatile int fps = 0;

    public int getFps() {
        return fps;
    }

    public void count()
    {
        if(System.currentTimeMillis() - frameCounterStartTime >= 1000)
        {
            fps = frameCounter;
            frameCounter = 1;
            frameCounterStartTime = System.currentTimeMillis();
        }
        else
        {
            frameCounter++;
        }
    }
}
