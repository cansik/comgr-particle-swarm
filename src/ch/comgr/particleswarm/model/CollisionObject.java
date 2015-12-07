package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.Tuple;
import ch.comgr.particleswarm.util.UpdateEventArgs;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roger on 06.12.2015.
 */
public class CollisionObject extends BaseSwarmObject implements ISimulationObject{

    private List<IMesh> meshes = new ArrayList<>();
    List<Vec3> vertexes = new ArrayList<>();

    Vec3 size = new Vec3(10, 20, 10);

    public CollisionObject(Vec3 startPos, String name) {
        super(startPos);

        Tuple<IMesh, List<Vec3>> tuple = EtherGLUtil.createFilledBox(size);

        // add mesh
        IMesh mesh = tuple.getFirst();
        mesh.setName(name);
        meshes.add(mesh);

        // creat bounding box
        IMesh boundingBox = EtherGLUtil.createBox(size).getFirst();
        boundingBox.setName("BoundingBox");
        meshes.add(boundingBox);

        //init BaseSwarmObject
        setName(name);
        setPosition(startPos, new Vec3(0, 0, 0));
        // set bounding box
        setVertices(tuple.getSecond());
    }

    private void setPosition(Vec3 pos, Vec3 velocity) {
        meshes.forEach((mesh) -> {
            mesh.setTransform(Mat4.translate(pos));
        });
    }

    @Override
    public List<IMesh> getMeshes() {
        return meshes;
    }

    @Override
    public void update(UpdateEventArgs args) {
        super.nextStep(args);

        updateBoundingBox();

        setPosition(position, new Vec3(0, 0, 0));
    }
}
