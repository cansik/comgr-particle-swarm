package ch.comgr.particleswarm.simulation;

import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.color.RGBA;

/**
 * Created by cansik on 20/10/15.
 */
public class Particle implements ISimulationObject {

    private IMesh mesh;
    private float angle = 0;
    private float ad = 0;

    public Particle(float ad)
    {
        this.ad = ad;
        mesh = MeshLibrary.createCube();
        //mesh = makeColoredTriangle(1);
    }

    private static IMesh makeColoredTriangle(float off) {
        float[] vertices = { off + 0, 0,
                off + 0, 0,
                off + 0, 0.5f,
                off + 0.5f, 0, 0.5f };
        float[] colors = { 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1 };

        DefaultGeometry g = DefaultGeometry.createVC(IGeometry.Primitive.TRIANGLES, vertices, colors);
        return new DefaultMesh(new ColorMaterial(RGBA.WHITE, true), g, IMesh.Queue.DEPTH);
    }

    @Override
    public IMesh getMesh() {
        return mesh;
    }

    @Override
    public void update() {
        //rotate mesh
        angle += ad;

        Mat4 transform = Mat4.multiply(Mat4.rotate(angle, Vec3.Z), Mat4.translate(1, 1, 0));
        mesh.setTransform(transform);
        mesh.requestUpdate(transform);
    }
}
