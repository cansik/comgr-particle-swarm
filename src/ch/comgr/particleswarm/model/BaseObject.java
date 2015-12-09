package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.controller.SwarmSimulation;
import ch.fhnw.ether.scene.I3DObject;
import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

import java.util.List;

/**
 * Created by Roger on 09.12.2015.
 */
public class BaseObject implements I3DObject {
    private Vec3 position;
    private String name;
    private final SwarmSimulation swarmSimulation;

    private List<Vec3> vertices;
    private BoundingBox boundingBox;

    public BaseObject(Vec3 startPosition, SwarmSimulation swarmSimulation) {
        this.position = startPosition;
        this.swarmSimulation = swarmSimulation;
    }

    public void updateBoundingBox() {
        boundingBox = new BoundingBox();
        //initBoundingBox
        for(Vec3 vec : vertices) {
            boundingBox.add(position.x + vec.x, position.y + vec.y, position.z + vec.z);
        }
    }

    public SwarmSimulation getSwarmSimulation() {
        return swarmSimulation;
    }

    public void setVertices(List<Vec3> vertices) {
        assert vertices != null;
        this.vertices = vertices;

        updateBoundingBox();
    }

    @Override
    public BoundingBox getBounds() {
        assert boundingBox != null;

        return boundingBox;
    }

    @Override
    public Vec3 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vec3 vec3) {
        this.position = vec3;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public UpdateRequest getUpdater() {
        return null;
    }
}
