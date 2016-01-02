package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.controller.SwarmSimulation;
import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.ObjectLoader;
import ch.comgr.particleswarm.util.Tuple;
import ch.comgr.particleswarm.util.UpdateEventArgs;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by cansik on 20/10/15.
 */
public class Particle extends BaseSwarmObject implements ISimulationObject {

    private List<IMesh> meshes = new ArrayList<>();

    float size = 1f;
    float neighbourRadius = 5f;
    Vec3 sizeVec3 = new Vec3(size,size,size);

    public Particle(Vec3 startPos, String name, SwarmSimulation swarmSimulation) {
        super(startPos, swarmSimulation);

        //cube mash
        //meshes.add(MeshLibrary.createCube());

        //meshes.add(EtherGLUtil.createSquarePyramid(5));
        //meshes.add(EtherGLUtil.createLine(velocity, 20));

        //add sphere mash instead of cube
        //meshes.add(EtherGLUtil.createSphere(1));

        // ad meshed from obj
        //getAndAddMeshesFromObj("spaceship.obj");
        //getAndAddMeshesFromObj("bunny.obj");

        // get swarm object
        IMesh mesh = EtherGLUtil.createSphere(size, getRandomColor());
        mesh.setName(name);
        meshes.add(mesh);

        // get swarm object bounding box
        Tuple<IMesh, List<Vec3>> tuple = EtherGLUtil.createSphereBox(sizeVec3);

        //init BaseSwarmObject
        setName(name);
        changePosition(startPos, velocity);
        // set bounding box
        setVertices(tuple.getSecond());
    }

    private RGBA getRandomColor(){
        Random rnd = new Random();
        return new RGBA(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 1.0f );
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
    private void changePosition(Vec3 pos, Vec3 velocity) {
        /*
        Mat4 transform = Mat4.multiply(Mat4.translate(pos),
                Mat4.lookAt(position, position.add(velocity), new Vec3(0, 0, 1)));
        */

        meshes.forEach((mesh) -> {
            //TODO: fix with nice solution
            if(mesh.getName().equals("BoundingBox"))
                mesh.setTransform(Mat4.multiply(calcTransformation(pos, velocity), Mat4.scale(neighbourRadius)));
            else
                mesh.setTransform(calcTransformation(pos, velocity));
        });
    }

    private Mat4 calcTransformation(Vec3 pos, Vec3 velocity) {
        float angleY = (float) Math.atan2(velocity.z, velocity.x);
        float angleX = (float) Math.atan2(velocity.y, Math.sqrt(Math.pow(velocity.x, 2) + Math.pow(velocity.z, 2)));

        float angleYinGrad = (float)Math.toDegrees(angleY);
        float angleXinGrad = (float)Math.toDegrees(angleX);

        return Mat4.multiply(Mat4.translate(pos),
                Mat4.rotate(angleXinGrad, Vec3.X),
                Mat4.rotate(angleYinGrad, Vec3.Y));
    }

    private void checkCollision(List<CollisionObject> collisionObjects) {
        for(CollisionObject obj : collisionObjects) {
            if(obj.getBounds().intersects(getBounds())) {
                Logger.getLogger("Collision").info("Collision detected");
                /**
                 * TODO what happend with the collision
                 */
            }
        }
    }

    private void setOrRemoveBoundingBoxMesh(boolean debugMode) {
        // create bounding box
        IMesh boundingBox = meshes.get(meshes.size() - 1);

        if(debugMode) {
            if(!"BoundingBox".equals(boundingBox.getName()))  {
                // add mesh
                boundingBox = EtherGLUtil.createSphereBox(sizeVec3).getFirst();
                boundingBox.setName("BoundingBox");
                meshes.add(boundingBox);

                /*
                //add neighbour radius
                //TODO: fix with nice solution
                IMesh neighbourMesh = EtherGLUtil.createSphere(neighbourRadius, new RGBA(0.875f, 0.353f, 0.286f, 0.1f));
                neighbourMesh.setName("neighbourMesh");
                meshes.add(neighbourMesh);
                */

                getSwarmSimulation().addMesh(boundingBox);
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
        super.nextStep(args);

        this.neighbourRadius = args.getNeighbourRadius();

        /*
        // crashes visualVM
        ArrayList<BaseSwarmObject> swarm = new ArrayList<>();

        for (ISimulationObject sim : simulationObjects)
            if (sim instanceof BaseSwarmObject)
                swarm.add((BaseSwarmObject) sim);
        */
        setOrRemoveBoundingBoxMesh(args.getDebugMode());

        updateBoundingBox();

        checkCollision(args.getCollisionObjects());

        changePosition(getPosition(), velocity);
    }

    public IMesh getMeshByName(String name)
    {
        for(IMesh m : meshes)
            if (m.getName().equals(name))
                return m;

        return null;
    }
}
