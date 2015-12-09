package ch.comgr.particleswarm.util;

import ch.comgr.particleswarm.model.CollisionObject;
import ch.comgr.particleswarm.model.ISimulationObject;

import java.util.List;

/**
 * Created by cansik on 10/11/15.
 */
public class UpdateEventArgs {
    private final float maxSpeed;
    private final float maxForce;

    private final float separationWeight;
    private final float alignmentWeight;
    private final float cohesionWeight;

    private final float desiredSeparation;
    private final float neighbourRadius;

    private final float boxWidth;
    private final float boxHeight;
    private final float boxDepth;

    private final List<ISimulationObject> simulationObjects;
    private final List<CollisionObject> collisionObjects;

    private final boolean debugMode;

    public UpdateEventArgs(float maxSpeed,
                           float maxForce,
                           float separationWeight,
                           float alignmentWeight,
                           float cohesionWeight,
                           float desiredSeparation,
                           float neighbourRadius,
                           float boxWidth,
                           float boxHeight,
                           float boxDepth,
                           boolean debugMode,
                           List<ISimulationObject> simulationObjects,
                           List<CollisionObject> collisionObjects)
    {
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.separationWeight = separationWeight;
        this.alignmentWeight = alignmentWeight;
        this.cohesionWeight = cohesionWeight;
        this.desiredSeparation = desiredSeparation;
        this.neighbourRadius = neighbourRadius;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.boxDepth = boxDepth;
        this.simulationObjects = simulationObjects;
        this.collisionObjects = collisionObjects;
        this.debugMode = debugMode;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getMaxForce() {
        return maxForce;
    }

    public float getSeparationWeight() {
        return separationWeight;
    }

    public float getAlignmentWeight() {
        return alignmentWeight;
    }

    public float getCohesionWeight() {
        return cohesionWeight;
    }

    public float getDesiredSeparation() {
        return desiredSeparation;
    }

    public float getNeighbourRadius() {
        return neighbourRadius;
    }

    public float getBoxWidth() {
        return boxWidth;
    }

    public float getBoxHeight() {
        return boxHeight;
    }

    public float getBoxDepth() {
        return boxDepth;
    }

    public List<ISimulationObject> getSimulationObjects() {
        return simulationObjects;
    }

    public List<CollisionObject> getCollisionObjects() {
        return collisionObjects;
    }

    public boolean getDebugMode() {
        return debugMode;
    }
}
