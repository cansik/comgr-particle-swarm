package ch.comgr.particleswarm.ui;

import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.ui.AbstractWidget;
import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.ui.Slider;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.math.MathUtilities;

import java.awt.*;

/**
 * Created by cansik on 13/11/15.
 */
    public class SwarmSlider extends Slider {

    private static final int SLIDER_WIDTH = 160;
    private static final int SLIDER_HEIGHT = 24;

    private static final int SLIDER_GAP = 8;

    private static final Color SLIDER_BG = new Color(1f, 1f, 1f, 0.25f);
    private static final Color SLIDER_FG = new Color(0.6f, 0, 0, 0.75f);

    private Color foregroundColor = this.SLIDER_FG;

    private boolean sliding;
    private float value;
    private float min = 0f;
    private float max = 1f;

    public SwarmSlider(int x, int y, String label, String help) {
        this(x, y, label, help, 0, null);
    }

    public SwarmSlider(int x, int y, String label, String help, float value) {
        this(x, y, label, help, value, null);
    }

    public SwarmSlider(int x, int y, String label, String help, float value, ISliderAction action) {
        super(x, y, label, help, value, action);
        this.value = value;
    }

    public SwarmSlider(int x, int y, String label, String help, float value, float min, float max, ISliderAction action) {
        super(x, y, label, help, value, action);
        this.value = value / max;
        this.min = min;
        this.max = max;
    }

    public SwarmSlider(int x, int y, String label, String help, float value, float min, float max, Color foregroundColor, ISliderAction action) {
        this(x, y, label, help, value, min, max, action);
        this.foregroundColor = foregroundColor;
    }

    public float getValue() {
        return value*this.max;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    @Override
    public boolean hit(IPointerEvent e) {
        int x = e.getX();
        int y = e.getY();
        UI ui = getUI();
        float bx = ui.getX() + getX() * (SLIDER_GAP + SLIDER_WIDTH);
        float by = ui.getY() + getY() * (SLIDER_GAP + SLIDER_HEIGHT);
        return x >= bx && x <= bx + SLIDER_WIDTH && y >= by && y <= by + SLIDER_HEIGHT;
    }

    @Override
    public void draw(GraphicsPlane surface) {
        int bw = SwarmSlider.SLIDER_WIDTH;
        int bh = SwarmSlider.SLIDER_HEIGHT;
        int bg = SwarmSlider.SLIDER_GAP;
        int bx = getX() * (bg + bw);
        int by = getY() * (bg + bh);
        surface.fillRect(SLIDER_BG, bx + 4, surface.getHeight() - by - bh - 4, bw, bh);
        surface.fillRect(foregroundColor, bx + 4, surface.getHeight() - by - bh - 4, (int) (value * bw), bh);
        String label = getLabel();
        if (label != null) {
            label += " (" + Math.round(getValue()*100.0)/100.0 +  ") ";
            surface.drawString(TEXT_COLOR, label, bx + 6, surface.getHeight() - by - 8);
        }
    }

    @Override
    public void fire(IView view) {
        if (getAction() == null)
            throw new UnsupportedOperationException("button '" + getLabel() + "' has no action defined");
        ((ISliderAction) getAction()).execute(this, view);
    }

    @Override
    public boolean pointerPressed(IPointerEvent e) {
        if (hit(e)) {
            sliding = true;
            updateValue(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean pointerReleased(IPointerEvent e) {
        if (sliding) {
            sliding = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean pointerDragged(IPointerEvent e) {
        if (sliding) {
            updateValue(e);
            return true;
        }
        return false;
    }

    private void updateValue(IPointerEvent e) {
        UI ui = getUI();
        float bx = ui.getX() + getX() * (SLIDER_GAP + SLIDER_WIDTH);
        value = MathUtilities.clamp((e.getX() - bx) / SLIDER_WIDTH, 0, 1);
        updateRequest();
        fire(e.getView());
    }
}
