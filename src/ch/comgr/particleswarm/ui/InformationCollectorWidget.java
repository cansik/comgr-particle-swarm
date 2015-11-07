package ch.comgr.particleswarm.ui;

import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.view.IView;

/**
 * Created by cansik on 05/11/15.
 */
public class InformationCollectorWidget extends ch.fhnw.ether.ui.AbstractWidget {

    private int fpsCounter;
    private int objectsCounter;
    private int meshesCounter;

    public InformationCollectorWidget(int x, int y, String text, String help) {
        super(x, y, text, help, null);
    }

    public void setFps(int fpsCounter) {
        this.fpsCounter = fpsCounter;
    }

    public void setObjectsCounter(int objectsCounter) {
        this.objectsCounter = objectsCounter;
    }

    public void setMeshesCounter(int meshesCounter) {
        this.meshesCounter = meshesCounter;
    }

    @Override
    public void draw(GraphicsPlane plane) {
        plane.drawString("Fps: " + this.fpsCounter, getX(), getY());
        plane.drawString("Meshes: " + this.meshesCounter, getX(), getY() - 20);
        plane.drawString("Objects: " + this.objectsCounter, getX(), getY() - 40);

        plane.drawString("[R] reset camera location", getX() + 180, getY() + 70);
        plane.drawString("[A/Q] change camera up and down (no rotation)", getX() + 180, getY() + 90);
        plane.drawString("[up/down/left/right] change camera (with rotation)" , getX() + 180, getY() + 110);
    }

    @Override
    public void fire(IView view) {
    }
}
