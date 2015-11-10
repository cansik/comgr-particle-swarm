package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.util.UpdateEventArgs;
import ch.fhnw.ether.scene.mesh.IMesh;

import java.util.List;

/**
 * Created by cansik on 20/10/15.
 */
public interface ISimulationObject {

    /**
     * Returns mashes of the simulation object.
     *
     * @return Mashes of the simulation object
     */
    List<IMesh> getMeshes();

    //todo: pass a wrapper object to store more information (parameter..)

    /**
     * Method to count a simulation object.
     *
     * @param args Simulation arguments.
     */
    void update(UpdateEventArgs args);

}
