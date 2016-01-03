package ch.comgr.particleswarm.controller;

import ch.comgr.particleswarm.ui.InformationCollectorWidget;
import ch.comgr.particleswarm.ui.SwarmButton;
import ch.comgr.particleswarm.ui.SwarmSlider;
import ch.comgr.particleswarm.util.SwarmConfiguration;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Vec3;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SwarmSimulationController extends DefaultController {
    private final SwarmSimulation swarmSimulation;
    private final SwarmConfiguration configuration;

    public final Camera Camera;
    private InformationCollectorWidget informationCollectorWidget;

    public SwarmSimulationController(
            SwarmSimulation swarmSimulation,
            SwarmConfiguration configuration){
        this.configuration = configuration;
        this.swarmSimulation = swarmSimulation;
        this.Camera = new Camera();
        setScene(new DefaultScene(this));

    }

    public InformationCollectorWidget getInformationCollectorWidget(){
        return informationCollectorWidget;
    }

    public void initView()
    {
        setupViewAndCamera();
        setupUi();
    }

    private void setupViewAndCamera()
    {
        DefaultView view = new DefaultView(this,
            configuration.ScreenPositionX,
            configuration.ScreenPositionY,
            configuration.ScreenWidth,
            configuration.ScreenHeight,
            IView.INTERACTIVE_VIEW,
            "Swarm Simulation");

        Camera.setPosition(configuration.DefaultCameraLocation);
        Camera.setUp(new Vec3(0, 0, 1));
        getScene().add3DObject(Camera);
        setCamera(view, Camera);
    }

    private void setupUi()
    {
        informationCollectorWidget = new InformationCollectorWidget(5, getUI().getHeight() - 180, "Information", "");
        getUI().addWidget(informationCollectorWidget);

        int xIndex = 0;
        addHorizontalButtonOnBottom(this, "Quit", KeyEvent.VK_ESCAPE, Color.BLUE, xIndex++, (button, v) -> System.exit(0));
        addHorizontalButtonOnBottom(this, "Collision", KeyEvent.VK_N, Color.BLUE, xIndex++, (button, v) -> swarmSimulation.addNewCollisionObject());
        addHorizontalButtonOnBottom(this, "Debug", KeyEvent.VK_D, Color.BLUE, xIndex++, (button, v) -> swarmSimulation.changeDebugMode());
        addHorizontalButtonOnBottom(this, "Misc", KeyEvent.VK_D, Color.BLUE, xIndex++, (button, v) -> swarmSimulation.toggleMisc());

        int yIndex = 1;
        addSliderVertical(this, configuration.NewNumberOfObjects, yIndex++);
        addSliderVertical(this, configuration.DesSeparation, yIndex++);
        addSliderVertical(this, configuration.NeighbourRadius, yIndex++);
        addSliderVertical(this, configuration.Separation, yIndex++);
        addSliderVertical(this, configuration.Alignment, yIndex++);
        addSliderVertical(this, configuration.Cohesion, yIndex++);
        addSliderVertical(this, configuration.MaxSpeed, yIndex++);
        addSliderVertical(this, configuration.MaxForce, yIndex++);
    }

    private void addHorizontalButtonOnBottom(IController controller, String name, int keyEventId, Color color, int xIndex, ch.fhnw.ether.ui.Button.IButtonAction buttonAction){
        SwarmButton btn = new SwarmButton(xIndex, 0, name, "", keyEventId, false, color, buttonAction);
        controller.getUI().addWidget(btn);
    }

    private void addSliderVertical(IController controller, SwarmConfiguration.FloatProperty property, int yIndex){
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

    @Override
    public void keyPressed(IKeyEvent e) {
        switch (e.getKeyCode()) {
            case IKeyEvent.VK_UP:
                Camera.setPosition(Camera.getPosition().add(Vec3.Y.scale(configuration.IncrementXy)));
                break;
            case IKeyEvent.VK_DOWN:
                Camera.setPosition(Camera.getPosition().add(Vec3.Y_NEG.scale(configuration.IncrementXy)));
                break;
            case IKeyEvent.VK_LEFT:
                Camera.setPosition(Camera.getPosition().add(Vec3.X_NEG.scale(configuration.IncrementXy)));
                break;
            case IKeyEvent.VK_RIGHT:
                Camera.setPosition(Camera.getPosition().add(Vec3.X.scale(configuration.IncrementXy)));
                break;
            case IKeyEvent.VK_Q:
                Camera.setPosition(Camera.getPosition().add(Vec3.Z.scale(configuration.IncrementZ)));
                break;
            case IKeyEvent.VK_A:
                Camera.setPosition(Camera.getPosition().add(Vec3.Z_NEG.scale(configuration.IncrementZ)));
                break;
            case IKeyEvent.VK_R:
                Camera.setPosition(configuration.DefaultCameraLocation);
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
