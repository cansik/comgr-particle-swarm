package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.controller.SwarmSimulation;
import ch.comgr.particleswarm.util.ObjectLoader;
import ch.fhnw.util.math.Vec3;

public class MiscObject extends Particle {
    public MiscObject(Vec3 startPos, String name, SwarmSimulation swarmSimulation) {
        super(startPos, name, swarmSimulation);
    }

    @Override
    protected void setupMesh(String name) {
        meshes.addAll(new ObjectLoader().getMeshesFromObject("../assets/spaceship.obj"));
    }
}
