package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.controller.SwarmSimulation;
import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.Tuple;
import ch.comgr.particleswarm.util.UpdateEventArgs;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Roger on 06.12.2015.
 */
public class CollisionObject extends BaseObject implements ISimulationObject{

    private List<IMesh> meshes = new ArrayList<>();

    Vec3 size = new Vec3(10, 20, 10);

    public CollisionObject(Vec3 startPos, String name, SwarmSimulation swarmSimulation) {
        super(startPos, swarmSimulation);

        Tuple<IMesh, List<Vec3>> tuple = EtherGLUtil.createFilledBox(size);

        // add mesh
        IMesh mesh = tuple.getFirst();
        mesh.setName(name);
        meshes.add(mesh);

        //init BaseSwarmObject
        setName(name);
        changePosition(startPos);
        // set bounding box
        setVertices(tuple.getSecond());
    }

    private void changePosition(Vec3 pos) {
        meshes.forEach((mesh) -> {
            mesh.setTransform(Mat4.translate(pos));
        });
    }

    public Vec3 calcNearestVec(Vec3 point) {
        float cx = Math.max(Math.min(point.x, getPosition().x + size.x), getPosition().x);
        float cy = Math.max(Math.min(point.y, getPosition().y + size.y), getPosition().y);
        float cz = Math.max(Math.min(point.z, getPosition().z + size.z), getPosition().z);

        return new Vec3(cx,cy,cz);
    }

    private void setOrRemoveBoundingBoxMesh(boolean debugMode) {
        // create bounding box
        IMesh boundingBox = meshes.get(meshes.size() - 1);

        if(debugMode) {
            if(!"BoundingBox".equals(boundingBox.getName()))  {
                boundingBox = EtherGLUtil.createBox(size).getFirst();
                boundingBox.setName("BoundingBox");
                meshes.add(boundingBox);

                getSwarmSimulation().addMesh(boundingBox);

                changePosition(getPosition());
            }
        } else {
            if("BoundingBox".equals(boundingBox.getName())) {
                meshes.remove(boundingBox);
            }
        }
    }

    @Override
    public List<IMesh> getMeshes() {
        return meshes;
    }

    @Override
    public void update(UpdateEventArgs args) {
        setOrRemoveBoundingBoxMesh(args.getDebugMode());

        updateBoundingBox();
    }
}
