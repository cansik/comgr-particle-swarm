package ch.comgr.particleswarm;

import ch.comgr.particleswarm.controller.SwarmSimulation;
import ch.comgr.particleswarm.gui.UIControlCenter;

public class Main {

    public static void main(String[] args) {
        UIControlCenter control =  new UIControlCenter();
        AddingSampleParameterChangedListener(control);
        control.createAndShow();

        SwarmSimulation simulation = new SwarmSimulation();
        simulation.run();
    }

    private static void AddingSampleParameterChangedListener(UIControlCenter c){
        // Test
        c.addParameterChangedListener(s -> {
            System.out.println(s.name() + ": "+ c.getParameterInformation(s).Value);
        });
    }
}
