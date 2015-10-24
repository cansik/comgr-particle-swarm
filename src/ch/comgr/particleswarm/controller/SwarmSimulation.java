package ch.comgr.particleswarm.controller;

import ch.comgr.particleswarm.model.ISimulationObject;
import ch.comgr.particleswarm.model.Particle;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Vec3;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by cansik on 20/10/15.
 */
public class SwarmSimulation {
    /************** Config-Variables ***************/

    // camera location increments
    private static final float INC_XY = 0.25f;
    private static final float INC_Z = 0.25f;
    // default camera location Vec3
    private static final Vec3 default_camera_location = new Vec3(5, 5, 3);
    // printable Help information
    private static final String[] HELP = {
            //@formatter:off
            "Swarm Simulator",
            "",
            "[up/down/left/right] to change camera location with axis-focus",
            "[A/Q] to change camera location up and down without rotation",
            "[R] reset camera location",
            "[H] print help",
            //@formatter:on
    };

    /************** Local Variables ***************/

    private IController controller;
    private Camera camera;
    private DefaultView view;
    private IScene scene;

    private ArrayList<ISimulationObject> simulationObjects;

    /*****************************/

    public SwarmSimulation()
    {
        // Create controller
        controller = new DefaultController() {
            @Override
            public void keyPressed(IKeyEvent e) {
                switch (e.getKey()) {
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
                    case IKeyEvent.VK_H:
                        printHelp(HELP);
                        break;
                    default:
                        super.keyPressed(e);
                }
                // update the camera system
                repaintViews();
            };
        };

        // Create view
        camera = new Camera();
        camera.setPosition(default_camera_location);
        camera.setUp(new Vec3(0, 0, 1));

        view = new DefaultView(controller, 100, 100, 500, 500, IView.INTERACTIVE_VIEW, "Swarm Simulation", camera);

        // Create scene
        scene = new DefaultScene(controller);
        controller.setScene(scene);

        // Add an exit button
        controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));

        simulationObjects = new ArrayList<>();
    }

    public void run() {

        //create initial scene
        addSimulationObject(new Particle(1));
        addSimulationObject(new Particle(-1));

        //main simulation loop
        controller.getScheduler().repeat(0, 1.0/60.0, (time, interval) -> {

            //update simulation logic for each element
            update();

            //repaint view
            controller.repaintViews();

            return true;
        });
    }

    private void update() {
        simulationObjects.parallelStream().forEach(o -> o.update(Collections.unmodifiableList(simulationObjects)));
    }

    private void addSimulationObject(ISimulationObject obj)
    {
        simulationObjects.add(obj);

        obj.getMeshes().forEach((iMesh ->
                scene.add3DObject(iMesh)
        ));
    }
}
