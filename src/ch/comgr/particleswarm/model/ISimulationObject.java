package ch.comgr.particleswarm.model;

import ch.fhnw.ether.scene.mesh.IMesh;

import java.util.List;

/**
 * Created by cansik on 20/10/15.
 */
public interface ISimulationObject {

    List<IMesh> getMeshes();

    void update();

}
