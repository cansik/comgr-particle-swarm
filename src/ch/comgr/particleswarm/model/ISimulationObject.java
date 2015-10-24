package ch.comgr.particleswarm.model;

import ch.fhnw.ether.scene.mesh.IMesh;

import java.util.List;

/**
 * Created by cansik on 20/10/15.
 */
public interface ISimulationObject {

    List<IMesh> getMeshes();

    //todo: pass a wrapper object to store more information (parameter..)
    void update(List<ISimulationObject> simulationObjects);

}
