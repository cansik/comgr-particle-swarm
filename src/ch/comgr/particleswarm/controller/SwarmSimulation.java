package ch.comgr.particleswarm.controller;

import ch.comgr.particleswarm.model.CollisionObject;
import ch.comgr.particleswarm.model.ISimulationObject;
import ch.comgr.particleswarm.model.MiscObject;
import ch.comgr.particleswarm.model.Particle;
import ch.comgr.particleswarm.util.*;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Vec3;
import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by cansik on 20/10/15.
 */
public class SwarmSimulation extends JFrame {
    private final SwarmConfiguration swarmConfiguration = new SwarmConfiguration();
    private final SwarmStatistics swarmStatistics = new SwarmStatistics();

    private SwarmSimulationController controller;
    private CopyOnWriteArrayList<ISimulationObject> simulationObjects;
    private CopyOnWriteArrayList<CollisionObject> collisionObjects;

    public SwarmSimulation() {
        swarmStatistics.NumOfObjects = (int) swarmConfiguration.NewNumberOfObjects.getCurrentValue();
        simulationObjects = new CopyOnWriteArrayList<>();
        collisionObjects = new CopyOnWriteArrayList<>();

        controller = new SwarmSimulationController(this, swarmConfiguration);
        controller.run((time) -> {
            // this code will run in another thread (UI Thread ?)
            controller.initView();
            controller.repaint();
            createInitialSimulationObjects();
        });
    }

    private void createInitialSimulationObjects() {
        controller.getScene().add3DObject(EtherGLUtil.createBox(new Vec3(100, 100, 100)).getFirst());
        for (int i = 0; i < swarmConfiguration.NewNumberOfObjects.getCurrentValue(); i++) {
            //start at 50, 50, 50
            addSimulationObject(i);
        }
    }

    /**
     * Initialises the simulation and starts the simulation loop.
     */
    public void run() {
        //main simulation loop
        controller.animate((time, interval) -> {
            updateInformationCollector();
            addOrRemoveSimulationObjectsOnConfigChange();
            updateSimulationObjects();
        });
    }

    private void updateInformationCollector() {
        swarmStatistics.FpsCounter.count();
        controller.getInformationCollectorWidget().setFps(swarmStatistics.FpsCounter.getFps());
        controller.getInformationCollectorWidget().setMeshesCounter(swarmStatistics.NumOfMeshes);
        controller.getInformationCollectorWidget().setObjectsCounter((int) swarmStatistics.NumOfObjects);
        controller.getUI().updateRequest();
    }

    private void addOrRemoveSimulationObjectsOnConfigChange() {
        int change = (int) ( swarmConfiguration.NewNumberOfObjects.getCurrentValue() - swarmStatistics.NumOfObjects);

        if (change > 0) {
            for (int i = change; i > 0; i--) {
                addSimulationObject(change);
                swarmStatistics.NumOfObjects++;
            }
        } else if(change < 0){
            removeSimulationObjects(Math.abs(change));
        }
    }

    /**
     * Updates all simulation objects.
     */
    private void updateSimulationObjects() {
        // create updateSimulationObjects event args
        UpdateEventArgs args = new UpdateEventArgs(swarmConfiguration,
                new ArrayList<>(simulationObjects),
                new ArrayList<>(collisionObjects));

        for (ISimulationObject o : simulationObjects) {
            o.update(args);
        }
    }

    public void addNewCollisionObject() {
        Vec3 distr = EtherGLUtil.randomVec3().scale(40f);
        Vec3 startVec = new Vec3(50, 50, 50);

        CollisionObject object = new CollisionObject(distr.add(startVec), "Collision object", this);
        collisionObjects.add(object);
        simulationObjects.add(object);
        addISimulationObjectMeshes(object);
        swarmStatistics.NumOfObjects++;
    }

    private void addISimulationObjectMeshes(ISimulationObject simulationObject){
        for (IMesh m : simulationObject.getMeshes()) {
            controller.getScene().add3DObject(m);
            swarmStatistics.NumOfMeshes++;
        }
    }

    private void removeISimulationObjectMeshes(ISimulationObject simulationObject){
        for (IMesh m : simulationObject.getMeshes()) {
            controller.getScene().remove3DObject(m);
            swarmStatistics.NumOfMeshes--;
        }
    }

    private MiscObject miscObject = null;
    public void toggleMisc(){
        if(miscObject == null){
            miscObject = new MiscObject(new Vec3(50,50,50), "Misc Object", this);
            simulationObjects.add(miscObject);
            addISimulationObjectMeshes(miscObject);
        }
        else{
            simulationObjects.remove(miscObject);
            removeISimulationObjectMeshes(miscObject);
            miscObject = null;
        }
    }

    public void changeDebugMode() {
        swarmConfiguration.DebugMode = !swarmConfiguration.DebugMode;
        if(!swarmConfiguration.DebugMode) {
            removeBoundingBoxMeshes();
        }
    }

    public void addMesh(IMesh m) {
        controller.getScene().add3DObject(m);
        swarmStatistics.NumOfMeshes++;
    }

    private void removeBoundingBoxMeshes() {
        for(IMesh m : controller.getScene().getMeshes()) {
            if("BoundingBox".equals(m.getName())) {
                controller.getScene().remove3DObject(m);
                swarmStatistics.NumOfMeshes--;
            }
        }
    }

    private void addSimulationObject(float name) {
        Vec3 distr = EtherGLUtil.randomVec3().scale(5f);
        Vec3 startVec = new Vec3(50, 50, 50);

        Particle particle = new Particle(distr.add(startVec), "Gen (" + (int) name + ")", this);
        simulationObjects.add(particle);
        addISimulationObjectMeshes(particle);
    }

    private void removeSimulationObjects(float number) {
        for(int i = 1; i < number + 1; i++) {
            int index = (int) swarmStatistics.NumOfObjects - i;

            ISimulationObject o = simulationObjects.get(index);
            simulationObjects.remove(o);
            removeISimulationObjectMeshes(o);
        }

        swarmStatistics.NumOfObjects -= number;
    }
}
