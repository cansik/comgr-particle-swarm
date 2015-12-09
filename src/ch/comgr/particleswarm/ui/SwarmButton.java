package ch.comgr.particleswarm.ui;

import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.ui.UI;

import java.awt.*;

/**
 * Created by cansik on 05/11/15.
 */
public class SwarmButton extends Button {

    private static final int BUTTON_WIDTH = 48;
    private static final int BUTTON_HEIGHT = 24;

    private static final int BUTTON_GAP = 8;

    private Color backgroundColor = new Color(0.6f, 0, 0, 0.75f);
    private Color pressedBackgroundColor = new Color(1, 0.2f, 0.2f, 0.75f);

    public SwarmButton(int x, int y, String label, String help, int key) {
        super(x, y, label, help, key, null);
    }

    public SwarmButton(int x, int y, String label, String help, int key, IButtonAction action) {
        super(x, y, label, help, key, action);
    }

    public SwarmButton(int x, int y, String label, String help, int key, State state, IButtonAction action) {
        super(x, y, label, help, key, action);
        setState(state);
    }

    public SwarmButton(int x, int y, String label, String help, int key, boolean pressed, IButtonAction action) {
        super(x, y, label, help, key, action);
        setState(pressed);
    }

    public SwarmButton(int x, int y, String label, String help, int key, boolean pressed, Color backgroundColor, IButtonAction action) {
        this(x, y, label, help, key, pressed, action);
        this.backgroundColor =backgroundColor;
        this.pressedBackgroundColor =backgroundColor.brighter();
    }

    @Override
    public boolean pointerPressed(IPointerEvent e) {
        if (hit(e))
            setState(true);
        return false;
    }


    @Override
    public boolean pointerReleased(IPointerEvent e) {
        setState(false);

        if (hit(e)) {
            fire(e.getView());
            return true;
        }
        return false;
    }

    @Override
    public boolean hit(IPointerEvent e) {
        int x = e.getX();
        int y = e.getY();
        UI ui = getUI();
        float bx = ui.getX() + getX() * (this.BUTTON_GAP + BUTTON_WIDTH);
        float by = ui.getY() + getY() * (BUTTON_GAP + BUTTON_HEIGHT);
        return x >= bx && x <= bx + BUTTON_WIDTH && y >= by && y <= by + BUTTON_HEIGHT;
    }

    @Override
    public void draw(GraphicsPlane surface) {
        int bw = SwarmButton.BUTTON_WIDTH;
        int bh = SwarmButton.BUTTON_HEIGHT;
        int bg = SwarmButton.BUTTON_GAP;
        int bx = getX() * (bg + bw);
        int by = getY() * (bg + bh);

        Color c = getState() == State.DEFAULT ? backgroundColor : pressedBackgroundColor;

        surface.fillRect(c, bx + 4, surface.getHeight() - by - bh - 4, bw, bh);
        String label = getLabel();
        if (label != null)
            surface.drawString(TEXT_COLOR, label, bx + 6, surface.getHeight() - by - 8);

    }

    @Override
    public boolean pointerMoved(IPointerEvent e) {
        return false;
    }

    @Override
    public boolean pointerDragged(IPointerEvent e) {
        return false;
    }
}
