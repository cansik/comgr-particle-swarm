package ch.comgr.particleswarm.simulation;

import ch.fhnw.ether.scene.mesh.IMesh;

/**
 * Created by cansik on 20/10/15.
 */
public interface ISimulationObject {

    IMesh getMesh();

    void update();

}
