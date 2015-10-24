package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.controller.ObjectLoader;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.color.RGBA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cansik on 20/10/15.
 */
public class Particle extends BaseSwarmObject {

    private List<IMesh> meshes = new ArrayList<>();

    public Particle()
    {
        meshes.add(MeshLibrary.createCube());
        //getAndAddMeshesFromObj();
    }

    private void getAndAddMeshesFromObj() {
        meshes.addAll(new ObjectLoader().getMeshesFromObject("../assets/bunny.obj"));

        // check if meshes were correct initialised
        assert meshes != null;
    }

    @Override
    public List<IMesh> getMeshes() {
        return meshes;
    }

    @Override
    public void update(List<ISimulationObject> simulationObjects) {
        super.update(simulationObjects);

        //update meshes
        Mat4 transform = Mat4.multiply(Mat4.rotate(angle, Vec3.Z), Mat4.translate(velocity));

        meshes.forEach((mesh) -> {
            mesh.setTransform(transform);
            mesh.requestUpdate(transform);
        });
    }
}
