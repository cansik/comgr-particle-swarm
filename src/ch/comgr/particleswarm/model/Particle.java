package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.ObjectLoader;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cansik on 20/10/15.
 */
public class Particle extends BaseSwarmObject implements ISimulationObject {

    private List<IMesh> meshes = new ArrayList<>();

    float a = 0f;

    public Particle(Vec3 startPos, String name) {
        super(startPos);

        //cube mash
        //meshes.add(MeshLibrary.createCube());

        meshes.add(EtherGLUtil.createSquarePyramid(5));
        meshes.add(EtherGLUtil.createLine(velocity, 20));

        //meshes.add(EtherGLUtil.createSphere(1));

        //add sphere mash instead of cube
        //meshes.add(EtherGLUtil.createSphere(1));

        // ad meshed from obj
        //getAndAddMeshesFromObj("bunny.obj");

        //set name
        meshes.get(0).setName(name);

        setPosition(startPos, velocity);
    }

    /**
     * Loads mesh from obj file.
     */
    private void getAndAddMeshesFromObj(String name) {
        meshes.addAll(new ObjectLoader().getMeshesFromObject("../assets/" + name));

        // check if meshes were correct initialised
        assert meshes != null;
    }

    /**
     * Sets the position of all meshes to the given position.
     *
     * @param pos Target position
     */
    private void setPosition(Vec3 pos, Vec3 velocity) {

        float angleY = (float) Math.atan2(velocity.z, velocity.x);
        float angleX = (float) Math.atan2(velocity.y, Math.sqrt(Math.pow(velocity.x, 2) + Math.pow(velocity.z, 2)));

        float angleYinGrad = (float)Math.toDegrees(angleY);
        float angleXinGrad = (float)Math.toDegrees(angleX);

        Mat4 transform = Mat4.multiply(Mat4.translate(pos),
                Mat4.rotate(angleXinGrad, Vec3.X),
                Mat4.rotate(angleYinGrad, Vec3.Y));

        /*
        Mat4 transform = Mat4.multiply(Mat4.translate(pos),
                Mat4.lookAt(position, position.add(velocity), new Vec3(0, 0, 1)));
        */

        meshes.forEach((mesh) -> {
            mesh.setTransform(transform);
        });
    }

    @Override
    public List<IMesh> getMeshes() {
        return meshes;
    }

    @Override
    public void update(List<ISimulationObject> simulationObjects) {
        super.nextStep(simulationObjects);

        /*
        // crashes visualVM
        ArrayList<BaseSwarmObject> swarm = new ArrayList<>();

        for (ISimulationObject sim : simulationObjects)
            if (sim instanceof BaseSwarmObject)
                swarm.add((BaseSwarmObject) sim);
        */

        setPosition(position, velocity);
    }
}
