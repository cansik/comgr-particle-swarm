package ch.comgr.particleswarm.ui;

import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.view.IView;

/**
 * Created by cansik on 05/11/15.
 */
public class Label extends ch.fhnw.ether.ui.AbstractWidget {

    private String text;

    public Label(int x, int y, String text, String help) {
        super(x, y, "", help, null);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(GraphicsPlane surface) {
        surface.drawString(this.text, getX(), getY());
    }

    @Override
    public void fire(IView view) {
    }
}
