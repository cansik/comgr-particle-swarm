package ch.comgr.particleswarm.ui;

import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.ui.GraphicsPlane;

/**
 * Created by cansik on 05/11/15.
 */
public class PushButton extends Button {

    private static final int BUTTON_WIDTH = 48;
    private static final int BUTTON_HEIGHT = 48;

    private static final int BUTTON_GAP = 8;

    public PushButton(int x, int y, String label, String help, int key) {
        super(x, y, label, help, key, null);
    }

    public PushButton(int x, int y, String label, String help, int key, IButtonAction action) {
        super(x, y, label, help, key, action);
    }

    public PushButton(int x, int y, String label, String help, int key, State state, IButtonAction action) {
        super(x, y, label, help, key, action);
        setState(state);
    }

    public PushButton(int x, int y, String label, String help, int key, boolean pressed, IButtonAction action) {
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
        int bw = PushButton.BUTTON_WIDTH;
        int bh = PushButton.BUTTON_HEIGHT;
        int bg = PushButton.BUTTON_GAP;
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
