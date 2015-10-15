package ch.comgr.particleswarm;

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

/**
 * Created by cansik on 15/10/15.
 */
public class SimpleCubeExample {
    public SimpleCubeExample() {
        // Create controller
        IController controller = new DefaultController();

        // Create view
        Camera camera = new Camera();
        camera.setPosition(new Vec3(0, 5, 0));
        camera.setUp(new Vec3(0, 0, 1));
        new DefaultView(controller, 100, 100, 500, 500, IView.INTERACTIVE_VIEW, "Simple Cube", camera);

        // Create scene and add a cube
        IScene scene = new DefaultScene(controller);
        controller.setScene(scene);

        scene.add3DObject(MeshLibrary.createCube());

        // Add an exit button
        controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
    }
}
