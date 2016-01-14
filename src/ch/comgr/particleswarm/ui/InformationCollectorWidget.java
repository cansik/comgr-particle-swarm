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
        int dw = 180, dh = 20;
        plane.drawString("Fps: " + this.fpsCounter, getX() + 180, getY() - dh);
        plane.drawString("Meshes: " + this.meshesCounter, getX() + 180, getY() - 20 - dh);
        plane.drawString("Objects: " + this.objectsCounter, getX() + 180, getY() - 40 - dh);

        plane.drawString("[D] debug mode", getX() + dw, getY() + 60);
        plane.drawString("[R] reset camera location", getX() + dw, getY() + 80);
        plane.drawString("[N] add new Collision Object", getX() + dw, getY() + 100);
        plane.drawString("[A/Q] change camera up and down (no rotation)", getX() + dw, getY() + 120);
        plane.drawString("[up/down/left/right] change camera (with rotation)" , getX() + dw, getY() + 140);
    }

    @Override
    public void fire(IView view) {
    }
}
