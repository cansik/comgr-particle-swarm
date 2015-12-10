package ch.comgr.particleswarm.controller;

import ch.comgr.particleswarm.model.CollisionObject;
import ch.comgr.particleswarm.model.ISimulationObject;
import ch.comgr.particleswarm.model.Particle;
import ch.comgr.particleswarm.ui.InformationCollectorWidget;
import ch.comgr.particleswarm.ui.SwarmButton;
import ch.comgr.particleswarm.ui.SwarmSlider;
import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.FPSCounter;
import ch.comgr.particleswarm.util.SwarmConfiguration;
import ch.comgr.particleswarm.util.UpdateEventArgs;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Vec3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by cansik on 20/10/15.
 */
public class SwarmSimulation extends JFrame {
    SwarmConfiguration swarmConfiguration = new SwarmConfiguration();
    private IController controller;
    private Camera camera;
    private IScene scene;

    private CopyOnWriteArrayList<ISimulationObject> simulationObjects;
    private CopyOnWriteArrayList<CollisionObject> collisionObjects;

    private FPSCounter fpsCounter = new FPSCounter();
    private InformationCollectorWidget informationCollectorWidget;

    private int numberOfMeshes;
    private float numberOfObjects = (int) swarmConfiguration.NewNumerOfObjects.getCurrentValue();

    public SwarmSimulation() {
        simulationObjects = new CopyOnWriteArrayList<>();
        collisionObjects = new CopyOnWriteArrayList<>();

        // Create controller
        controller = new SwarmSimulationController(camera, swarmConfiguration);
        controller.run((time) -> {
            // Create view
            camera = new Camera();
            camera.setPosition(swarmConfiguration.DefaultCameraLocation);
            camera.setUp(new Vec3(0, 0, 1));

            DefaultView simulationView = new DefaultView(controller,
                    swarmConfiguration.ScreenPositionX,
                    swarmConfiguration.ScreenPositionY,
                    swarmConfiguration.ScreenWidth,
                    swarmConfiguration.ScreenHeight,
                    IView.INTERACTIVE_VIEW,
                    "Swarm Simulation");

            // Create scene
            scene = new DefaultScene(controller);
            controller.setScene(scene);

            // add camera
            scene.add3DObject(camera);
            controller.setCamera(simulationView, camera);

            int xIndex = 0;
            AddHorizontalButtonOnBottom(controller, "Quit", KeyEvent.VK_ESCAPE, Color.BLUE, xIndex++, (button, v) -> System.exit(0));
            AddHorizontalButtonOnBottom(controller, "New", KeyEvent.VK_N, Color.BLUE, xIndex++, (button, v) -> addNewCollisionObject());
            AddHorizontalButtonOnBottom(controller, "New", KeyEvent.VK_D, Color.BLUE, xIndex++, (button, v) -> changeDebugMode());

            // Add information collector
            informationCollectorWidget = new InformationCollectorWidget(5, controller.getUI().getHeight() - 180, "Information", "");
            controller.getUI().addWidget(informationCollectorWidget);

            int yIndex = 1;
            AddSliderVertical(controller, swarmConfiguration.NewNumerOfObjects, yIndex++);
            AddSliderVertical(controller, swarmConfiguration.DesSeparation, yIndex++);
            AddSliderVertical(controller, swarmConfiguration.NeighbourRadius, yIndex++);
            AddSliderVertical(controller, swarmConfiguration.Separation, yIndex++);
            AddSliderVertical(controller, swarmConfiguration.Alignment, yIndex++);
            AddSliderVertical(controller, swarmConfiguration.Cohesion, yIndex++);
            AddSliderVertical(controller, swarmConfiguration.MaxSpeed, yIndex++);
            AddSliderVertical(controller, swarmConfiguration.MaxForce, yIndex++);

            // count the camera system
            controller.repaint();
            createInitialSimulationObjects();
        });
    }

    private void AddHorizontalButtonOnBottom(IController controller, String name, int keyEventId, Color color, int xIndex, Button.IButtonAction buttonAction){
        SwarmButton btn = new SwarmButton(xIndex, 0, name, "", keyEventId, false, color, buttonAction);
        controller.getUI().addWidget(btn);
    }

    private void AddSliderVertical(IController controller, SwarmConfiguration.FloatProperty property, int yIndex){
        SwarmSlider slider = new SwarmSlider(
                0, //yIndex
                yIndex,
                property.Name,
                "", // Description
                property.getCurrentValue(), // Value
                0f, // MinValue
                property.getMaxValue(),
                property.Color,
                (s, view) -> property.setCurrentValue(s.getValue())); // Action

        controller.getUI().addWidget(slider);
    }

    private void createInitialSimulationObjects() {
        scene.add3DObject(EtherGLUtil.createBox(new Vec3(100, 100, 100)).getFirst());
        for (int i = 0; i < swarmConfiguration.NewNumerOfObjects.getCurrentValue(); i++) {
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

    private void addOrRemoveSimulationObjectsOnConfigChange() {
        int change = (int) ( swarmConfiguration.NewNumerOfObjects.getCurrentValue() - numberOfObjects);

        if (change > 0) {
            for (int i = change; i > 0; i--) {
                addSimulationObject(change);
                numberOfObjects++;
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

    private void updateInformationCollector() {
        fpsCounter.count();
        informationCollectorWidget.setFps(fpsCounter.getFps());
        informationCollectorWidget.setMeshesCounter(numberOfMeshes);
        informationCollectorWidget.setObjectsCounter((int) numberOfObjects);
        controller.getUI().updateRequest();
    }

    private void addNewCollisionObject() {
        Vec3 distr = EtherGLUtil.randomVec3().scale(40f);
        Vec3 startVec = new Vec3(50, 50, 50);

        CollisionObject object = new CollisionObject(distr.add(startVec), "Collision object", this);
        collisionObjects.add(object);
        simulationObjects.add(object);

        for (IMesh m : object.getMeshes()) {
            scene.add3DObject(m);
            numberOfMeshes++;
        }
        numberOfObjects++;
    }

    private void changeDebugMode() {
        swarmConfiguration.DebugMode = !swarmConfiguration.DebugMode;

        if(!swarmConfiguration.DebugMode) {
            removeBoundingBoxMeshs();
        }
    }

    public void addMesh(IMesh m) {
        scene.add3DObject(m);
        numberOfMeshes++;
    }

    private void removeBoundingBoxMeshs() {
        for(IMesh m : scene.getMeshes()) {
            if("BoundingBox".equals(m.getName())) {
                scene.remove3DObject(m);
                numberOfMeshes--;
            }
        }
    }

    private void addSimulationObject(float name) {
        Vec3 distr = EtherGLUtil.randomVec3().scale(5f);
        Vec3 startVec = new Vec3(50, 50, 50);

        Particle particle = new Particle(distr.add(startVec), "Gen (" + (int) name + ")", this);
        simulationObjects.add(particle);

        for (IMesh m : particle.getMeshes()) {
            scene.add3DObject(m);
            numberOfMeshes++;
        }
    }

    private void removeSimulationObjects(float number) {
        for(int i = 1; i < number + 1; i++) {
            int index = (int) numberOfObjects - i;

            ISimulationObject o = simulationObjects.get(index);
            simulationObjects.remove(o);

            for (IMesh m : o.getMeshes()) {
                scene.remove3DObject(m);
                numberOfMeshes--;
            }
        }

        numberOfObjects -= number;
    }
}
