package ch.comgr.particleswarm.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class UIControlCenter {
    private final java.util.List<ParameterInformation> parameters;
    private final JFrame frame = new JFrame("ParticleSwarm Control Center");
    private final HashSet<ParameterChangedListener> listeners;

    public UIControlCenter(){
        listeners = new HashSet<ParameterChangedListener>();
        parameters = new LinkedList<ParameterInformation>();
        parameters.add(new ParameterInformation(Parameters.MaxNumberOfObjects, 1000, s -> Integer.parseInt(s)));

        // Test
        listeners.add(s -> {
            System.out.println(s.name());
            System.out.println(getParameterInformation(s).Value);
        });
    }
    public void createAndShow(){

        frame.setSize(new Dimension(300, 800));
        frame.setLayout(new GridLayout(0,2));
        createControl(parameters.get(0));
        frame.setVisible(true);
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
        if(listeners.size() == 0) return;
        listeners.forEach(l -> l.action(p.Parameter));
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