package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.controller.ObjectLoader;
import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.geometry.GeodesicSphere;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cansik on 20/10/15.
 */
public class Particle extends BaseSwarmObject implements ISimulationObject {

    private List<IMesh> meshes = new ArrayList<>();

    public Particle(Vec3 startPos, String name) {
        super(startPos);

        //cube mash
        //meshes.add(MeshLibrary.createCube());

        //add sphere mash instead of cube
        meshes.add(EtherGLUtil.createSphere(1));

        //set name
        meshes.get(0).setName(name);

        //getAndAddMeshesFromObj();

        setPosition(startPos);
    }

    /**
     * Loads mesh from obj file.
     */
    private void getAndAddMeshesFromObj() {
        meshes.addAll(new ObjectLoader().getMeshesFromObject("../assets/bunny.obj"));

        // check if meshes were correct initialised
        assert meshes != null;
    }

    /**
     * Sets the position of all meshes to the given position.
     *
     * @param pos Target position
     */
    private void setPosition(Vec3 pos) {
        //todo: add object rotation
        Mat4 transform = Mat4.translate(pos);

        meshes.forEach((mesh) -> {
            mesh.setTransform(transform);
            mesh.updateRequest();
        });
    }

    @Override
    public List<IMesh> getMeshes() {
        return meshes;
    }

    @Override
    public void update(List<ISimulationObject> simulationObjects) {
        super.nextStep(simulationObjects);

        //update meshes
        setPosition(position);
    }
}
