package ch.comgr.particleswarm.util;

import ch.comgr.particleswarm.model.BaseSwarmObject;
import ch.comgr.particleswarm.model.CollisionObject;
import ch.comgr.particleswarm.model.ISimulationObject;

import java.util.List;

/**
 * Created by cansik on 10/11/15.
 */
public class UpdateEventArgs {
    private final List<ISimulationObject> simulationObjects;
    private final List<CollisionObject> collisionObjects;
    private final SwarmConfiguration configuration;
    private final List<BaseSwarmObject> baseSwarmObjects;
    private final GridCoordination gridCoordination;

    public UpdateEventArgs(SwarmConfiguration configuration,
                           List<ISimulationObject> simulationObjects,
                           List<CollisionObject> collisionObjects,
                           List<BaseSwarmObject> baseSwarmObjects,
                           GridCoordination gridCoordination)
    {
        this.configuration = configuration;
        this.simulationObjects = simulationObjects;
        this.collisionObjects = collisionObjects;
        this.baseSwarmObjects = baseSwarmObjects;
        this.gridCoordination = gridCoordination;
    }

    public GridCoordination getGridCoordination() {return gridCoordination; }

    public List<BaseSwarmObject> getBaseSwarmObjects() { return baseSwarmObjects; }

    public float getMaxSpeed() { return configuration.MaxSpeed.getCurrentValue();}

    public float getMaxForce() { return configuration.MaxForce.getCurrentValue();}

    public float getSeparationWeight() { return configuration.Separation.getCurrentValue();}

    public float getAlignmentWeight() {
        return configuration.Alignment.getCurrentValue();
    }

    public float getCohesionWeight() {
        return configuration.Cohesion.getCurrentValue();
    }

    public float getDesiredSeparation() {
        return configuration.DesSeparation.getCurrentValue();
    }

    public float getNeighbourRadius() {
        return configuration.NeighbourRadius.getCurrentValue();
    }

    public float getBoxWidth() {
        return configuration.BoxWidth.getCurrentValue();
    }

    public float getBoxHeight() {
        return configuration.BoxWidth.getCurrentValue();
    }

    public float getBoxDepth() {
        return configuration.BoxDepth.getCurrentValue();
    }

    public List<ISimulationObject> getSimulationObjects() {
        return simulationObjects;
    }

    public List<CollisionObject> getCollisionObjects() {
        return collisionObjects;
    }

    public boolean getDebugMode()  {
        return configuration.DebugMode;
    }
}
