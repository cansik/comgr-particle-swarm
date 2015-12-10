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
        informationCollectorWidget = new InformationCollectorWidget(5, getUI().getHeight() - 180, "Information", "");
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
        getUI().addWidget(informationCollectorWidget);

        int xIndex = 0;
        AddHorizontalButtonOnBottom(this, "Quit", KeyEvent.VK_ESCAPE, Color.BLUE, xIndex++, (button, v) -> System.exit(0));
        AddHorizontalButtonOnBottom(this, "New", KeyEvent.VK_N, Color.BLUE, xIndex++, (button, v) -> swarmSimulation.addNewCollisionObject());
        AddHorizontalButtonOnBottom(this, "New", KeyEvent.VK_D, Color.BLUE, xIndex++, (button, v) -> swarmSimulation.changeDebugMode());

        int yIndex = 1;
        AddSliderVertical(this, configuration.NewNumerOfObjects, yIndex++);
        AddSliderVertical(this, configuration.DesSeparation, yIndex++);
        AddSliderVertical(this, configuration.NeighbourRadius, yIndex++);
        AddSliderVertical(this, configuration.Separation, yIndex++);
        AddSliderVertical(this, configuration.Alignment, yIndex++);
        AddSliderVertical(this, configuration.Cohesion, yIndex++);
        AddSliderVertical(this, configuration.MaxSpeed, yIndex++);
        AddSliderVertical(this, configuration.MaxForce, yIndex++);
    }

    private void AddHorizontalButtonOnBottom(IController controller, String name, int keyEventId, Color color, int xIndex, ch.fhnw.ether.ui.Button.IButtonAction buttonAction){
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
