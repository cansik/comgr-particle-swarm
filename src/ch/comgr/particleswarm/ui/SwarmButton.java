package ch.comgr.particleswarm.ui;

import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.ui.GraphicsPlane;

/**
 * Created by cansik on 05/11/15.
 */
public class SwarmButton extends Button {

    private static final int BUTTON_WIDTH = 48;
    private static final int BUTTON_HEIGHT = 24;

    private static final int BUTTON_GAP = 8;

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
    public void draw(GraphicsPlane surface) {
        int bw = SwarmButton.BUTTON_WIDTH;
        int bh = SwarmButton.BUTTON_HEIGHT;
        int bg = SwarmButton.BUTTON_GAP;
        int bx = getX() * (bg + bw);
        int by = getY() * (bg + bh);
        surface.fillRect(getState().getColor(), bx + 4, surface.getHeight() - by - bh - 4, bw, bh);
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
