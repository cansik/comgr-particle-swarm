package ch.comgr.particleswarm.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class UIControlCenter {
    private final java.util.List<ParameterInformation> parameters = new LinkedList<ParameterInformation>();
    private final JFrame frame = new JFrame("ParticleSwarm Control Center");
    private final HashSet<ParameterChangedListener> listeners = new HashSet<ParameterChangedListener>();
    private final boolean debug = false;

    public UIControlCenter(){
        definingParametersForGuiControl();
        parameters.stream().forEach(p -> createControl(p));
    }

    public void addParameterChangedListener(ParameterChangedListener l){
        if(listeners.contains(l)) return;
        listeners.add(l);
    }

    public void createAndShow(){
        frame.setSize(new Dimension(300, 800));
        frame.setLayout(new GridLayout(0,2));
        frame.setVisible(true);
    }

    private void definingParametersForGuiControl(){
        parameters.add(new ParameterInformation(Parameters.MaxNumberOfObjects, 1000, s -> Integer.parseInt(s)));
        parameters.add(new ParameterInformation(Parameters.AlignmentWeight, 2.3f, s -> Double.parseDouble(s)));
    }

    public ParameterInformation getParameterInformation(Parameters p){
        return parameters
                .stream()
                .filter(pr -> pr.Parameter == p)
                .findFirst()
                .get();
    }

    private void createControl(ParameterInformation parameterInformation){
        frame.add(new JLabel(parameterInformation.Parameter.name()));
        JTextField control = new JTextField(parameterInformation.Value.toString());
        control.addKeyListener(new CustomKeyListener(parameterInformation));
        frame.add(control);
    }

    private void propagateChange(ParameterInformation p){
        if(listeners.size() == 0)
            return;

        listeners.forEach(l -> l.action(p.Parameter));

        if(debug)
            System.out.println(p.Parameter.name() + ": "+ getParameterInformation(p.Parameter).Value);
    }

    private class CustomKeyListener implements KeyListener{
        private final ParameterInformation parameterInformation;

        public CustomKeyListener(ParameterInformation parameterInformation){
            this.parameterInformation = parameterInformation;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            tryParseAndCallAction(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            tryParseAndCallAction(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            tryParseAndCallAction(e);
        }

        private void tryParseAndCallAction(KeyEvent e){
            JTextField textField = (JTextField) e.getComponent();
            try{
                parameterInformation.Value = parameterInformation.Converter.convert(textField.getText());
                propagateChange(parameterInformation);
            }
            catch (Exception ex){
                System.out.println(ex.toString());
            }
        }

    }

}