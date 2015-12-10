package ch.comgr.particleswarm.controller;

import ch.comgr.particleswarm.util.SwarmConfiguration;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.util.math.Vec3;

public class SwarmSimulationController extends DefaultController {
    private final ICamera camera;
    private final SwarmConfiguration configuration;

    public SwarmSimulationController(ICamera camera, SwarmConfiguration configuration){
        this.camera = camera;
        this.configuration = configuration;
    }

    @Override
    public void keyPressed(IKeyEvent e) {
        switch (e.getKeyCode()) {
            case IKeyEvent.VK_UP:
                camera.setPosition(camera.getPosition().add(Vec3.Y.scale(configuration.IncrementXy)));
                break;
            case IKeyEvent.VK_DOWN:
                camera.setPosition(camera.getPosition().add(Vec3.Y_NEG.scale(configuration.IncrementXy)));
                break;
            case IKeyEvent.VK_LEFT:
                camera.setPosition(camera.getPosition().add(Vec3.X_NEG.scale(configuration.IncrementXy)));
                break;
            case IKeyEvent.VK_RIGHT:
                camera.setPosition(camera.getPosition().add(Vec3.X.scale(configuration.IncrementXy)));
                break;
            case IKeyEvent.VK_Q:
                camera.setPosition(camera.getPosition().add(Vec3.Z.scale(configuration.IncrementZ)));
                break;
            case IKeyEvent.VK_A:
                camera.setPosition(camera.getPosition().add(Vec3.Z_NEG.scale(configuration.IncrementZ)));
                break;
            case IKeyEvent.VK_R:
                camera.setPosition(configuration.DefaultCameraLocation);
                break;
            case IKeyEvent.VK_B:
                if(configuration.ShowController) {
                    this.getUI().disable();
                    configuration.ShowController = false;
                }
                else
                {
                    this.getUI().enable();
                    configuration.ShowController = true;
                }
                break;
            default:
                super.keyPressed(e);
        }
    }
}
