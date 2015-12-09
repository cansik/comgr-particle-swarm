package ch.comgr.particleswarm.controller;

import ch.comgr.particleswarm.model.CollisionObject;
import ch.comgr.particleswarm.model.ISimulationObject;
import ch.comgr.particleswarm.model.Particle;
import ch.comgr.particleswarm.ui.InformationCollectorWidget;
import ch.comgr.particleswarm.ui.SwarmButton;
import ch.comgr.particleswarm.ui.SwarmSlider;
import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.FPSCounter;
import ch.comgr.particleswarm.util.UpdateEventArgs;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.ui.ParameterWindow;
import ch.fhnw.ether.ui.Slider;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Vec3;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Created by cansik on 20/10/15.
 */
public class SwarmSimulation extends JFrame {
    /**************
     * Config-Variables
     ***************/

    // simulation loop interval
    private static final double LOOP_INTERVAL = 0.5; //1.0 / 60.0;
    // camera location increments
    private static final float INC_XY = 0.25f;
    private static final float INC_Z = 0.25f;
    // screen properties
    private static final int screenPositionX = 10;
    private static final int screenPositionY = 40;
    private static final int screenWidth = 800;
    private static final int screenHeight = 800;
    private static final int maxNumberOfObjects = 1000;
    private static final float initialNumberOfObjects = 15;
    // default camera location Vec3
    private static final Vec3 default_camera_location = new Vec3(200, 200, 100);

    /**************
     * Local Variables
     ***************/

    private IController controller;
    private Camera camera;
    private DefaultView simulationView;
    private IScene scene;
    private IMesh box;

    private CopyOnWriteArrayList<ISimulationObject> simulationObjects;
    private CopyOnWriteArrayList<CollisionObject> collisionObjects;

    private FPSCounter fpsCounter = new FPSCounter();
    private InformationCollectorWidget informationCollectorWidget;

    private int numberOfMeshes;
    private float numberOfObjects = (int) initialNumberOfObjects;

    private boolean showController = true;
    private boolean debugMode = false;

    /***************
     * Parameter Variables
     ***************/

    private float newNumberOfObjects = (int) initialNumberOfObjects;

    private float maxSpeed = 0.5f;
    private float maxForce = 0.03f;

    private volatile float separationWeight = 1.5f;
    private float alignmentWeight = 1.0f;
    private float cohesionWeight = 1.0f;

    private volatile float desiredSeparation = 5.0f;
    private float neighbourRadius = 15.0f;

    private float boxWidth = 100f;
    private float boxHeight = 100f;
    private float boxDepth = 100f;

    /*****************************/

    public SwarmSimulation() {
        simulationObjects = new CopyOnWriteArrayList<>();
        collisionObjects = new CopyOnWriteArrayList<>();

        // Create controller
        controller = new DefaultController() {
            @Override
            public void keyPressed(IKeyEvent e) {
                switch (e.getKeyCode()) {
                    case IKeyEvent.VK_UP:
                        camera.setPosition(camera.getPosition().add(Vec3.Y.scale(INC_XY)));
                        break;
                    case IKeyEvent.VK_DOWN:
                        camera.setPosition(camera.getPosition().add(Vec3.Y_NEG.scale(INC_XY)));
                        break;
                    case IKeyEvent.VK_LEFT:
                        camera.setPosition(camera.getPosition().add(Vec3.X_NEG.scale(INC_XY)));
                        break;
                    case IKeyEvent.VK_RIGHT:
                        camera.setPosition(camera.getPosition().add(Vec3.X.scale(INC_XY)));
                        break;
                    case IKeyEvent.VK_Q:
                        camera.setPosition(camera.getPosition().add(Vec3.Z.scale(INC_Z)));
                        break;
                    case IKeyEvent.VK_A:
                        camera.setPosition(camera.getPosition().add(Vec3.Z_NEG.scale(INC_Z)));
                        break;
                    case IKeyEvent.VK_R:
                        camera.setPosition(default_camera_location);
                        break;
                    case IKeyEvent.VK_B:
                        if(showController) {
                            controller.getUI().disable();
                            showController = false;
                        }
                        else
                        {
                            controller.getUI().enable();
                            showController = true;
                        }
                        break;
                    default:
                        super.keyPressed(e);
                }
            }
        };

        controller.run((time) -> {
            // Create view
            camera = new Camera();
            camera.setPosition(default_camera_location);
            camera.setUp(new Vec3(0, 0, 1));

            simulationView = new DefaultView(controller, screenPositionX, screenPositionY, screenWidth, screenHeight, IView.INTERACTIVE_VIEW, "Swarm Simulation");

            // Create scene
            scene = new DefaultScene(controller);
            controller.setScene(scene);

            // add camera
            scene.add3DObject(camera);
            controller.setCamera(simulationView, camera);

            // Add exit button
            controller.getUI().addWidget(new SwarmButton(0, 0, "Quit", "", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));

            // Add object button
            controller.getUI().addWidget(new SwarmButton(1, 0, "New", "", KeyEvent.VK_N, (button, v) -> addNewCollisionObject()));

            // Add object button
            controller.getUI().addWidget(new SwarmButton(2, 0, "Debug", "", KeyEvent.VK_D, (button, v) -> changeDebugMode()));

            // Add information collector
            informationCollectorWidget = new InformationCollectorWidget(5, controller.getUI().getHeight() - 180, "Information", "");
            controller.getUI().addWidget(informationCollectorWidget);

            // add Slider
            Slider slider = new Slider(0, 1, "Objects", "", 1 / (maxNumberOfObjects / numberOfObjects), (s, view) -> newNumberOfObjects = (s.getValue() * maxNumberOfObjects));
            controller.getUI().addWidget(slider);

            SwarmSlider desiredSeparationSlider = new SwarmSlider(0, 2, "Desired Separation", "", 1 / desiredSeparation, 0f, 20f, (s, view) -> desiredSeparation = s.getValue());
            controller.getUI().addWidget(desiredSeparationSlider);

            // count the camera system
            controller.repaint();

            // initialise GameObjects
            initialiseScene();
        });
    }

    public void initialiseScene() {
        scene.add3DObject(EtherGLUtil.createBox(new Vec3(100, 100, 100)).getFirst());

        //create initial scene
        for (int i = 0; i < initialNumberOfObjects; i++) {
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
            // update information collector
            updateInformationCollector();

            // update slider and objects in the scene
            updateNumberOfObjects();

            // count simulation logic for each element
            update();
        });
    }

    private void updateNumberOfObjects() {
        int change = (int) (newNumberOfObjects - numberOfObjects);

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
    private void update() {
        // create update event args
        UpdateEventArgs args = new UpdateEventArgs(
                maxSpeed,
                maxForce,
                separationWeight,
                alignmentWeight,
                cohesionWeight,
                desiredSeparation,
                neighbourRadius,
                boxWidth,
                boxHeight,
                boxDepth,
                debugMode,
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
        debugMode = !debugMode;

        if(!debugMode) {
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
