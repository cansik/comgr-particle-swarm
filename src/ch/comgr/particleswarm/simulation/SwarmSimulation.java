package ch.comgr.particleswarm.simulation;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Vec3;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by cansik on 20/10/15.
 */
public class SwarmSimulation {
    private IController controller;
    private Camera camera;
    private DefaultView view;
    private IScene scene;

    private ArrayList<ISimulationObject> simulationObjects;

    public SwarmSimulation()
    {
        // Create controller
        controller = new DefaultController();

        // Create view
        Camera camera = new Camera();
        camera.setPosition(new Vec3(0, 5, 0));
        camera.setUp(new Vec3(0, 0, 1));

        view = new DefaultView(controller, 100, 100, 500, 500, IView.INTERACTIVE_VIEW, "Swarm Simulation", camera);

        // Create scene
        scene = new DefaultScene(controller);
        controller.setScene(scene);

        // Add an exit button
        controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));

        simulationObjects = new ArrayList<>();
    }

    public void run()
    {
        //create initial scene
        addSimulationObject(new Particle());

        //main simulation loop
        controller.getScheduler().repeat(0, 1.0/60.0, (time, interval) -> {

            //update simulation logic for each element
            update();

            //repaint view
            controller.repaintViews();
            return true;
        });
    }

    private void update()
    {
        simulationObjects.parallelStream().forEach(ISimulationObject::update);
    }

    private void addSimulationObject(ISimulationObject obj)
    {
        simulationObjects.add(obj);
        scene.add3DObject(obj.getMesh());
    }
}
