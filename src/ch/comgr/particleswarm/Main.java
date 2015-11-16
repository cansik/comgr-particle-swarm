package ch.comgr.particleswarm;

import ch.comgr.particleswarm.controller.SwarmSimulation;
import ch.comgr.particleswarm.gui.UIControlCenter;

public class Main {

    public static void main(String[] args) {
        UIControlCenter control =  new UIControlCenter();
        control.createAndShow();

//        SwarmSimulation simulation = new SwarmSimulation();
//        simulation.run();
    }
}
