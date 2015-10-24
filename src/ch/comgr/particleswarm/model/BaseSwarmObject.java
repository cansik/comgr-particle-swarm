package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.fhnw.util.math.Vec3;

import java.util.List;

/**
 * Created by cansik on 24/10/15.
 */
public abstract class BaseSwarmObject implements ISimulationObject {

    //todo: change this default values in the UI
    private static final float MAX_SPEED = 2.0f;
    private static final float MAX_FORCE = 0.03f;

    private static final float SPEARATION_WEIGHT = 1.5f;
    private static final float ALIGNMENT_WEIGHT = 1.0f;
    private static final float COHESION_WEIGHT = 1.0f;

    float angle;
    Vec3 position;
    Vec3 velocity;

    public BaseSwarmObject()
    {
        //get random spawn angle
        angle = (float)(Math.random()* EtherGLUtil.TWO_PI);

        //start from position zero
        position = Vec3.ZERO;

        //set velocity to initial amount
        velocity = new Vec3((float)Math.cos(angle), (float)Math.sin(angle), (float)Math.tan(angle));
    }

    @Override
    public void update(List<ISimulationObject> simulationObjects) {
        //calculate new acceleration
        Vec3 acceleration = calculateAcceleration(simulationObjects);

        //apply acceleration to velocity and update position
        velocity = velocity.add(acceleration);
        velocity = EtherGLUtil.limit(velocity, MAX_SPEED);

        position = position.add(velocity);
    }

    Vec3 calculateAcceleration(List<ISimulationObject> simulationObjects)
    {
        //calculate forces
        Vec3 separation = calculateSeparation(simulationObjects);
        Vec3 alignment = calculateSeparation(simulationObjects);
        Vec3 cohesion = calculateSeparation(simulationObjects);

        //weight the forces
        separation = separation.scale(SPEARATION_WEIGHT);
        alignment = alignment.scale(ALIGNMENT_WEIGHT);
        cohesion = cohesion.scale(COHESION_WEIGHT);

        //generate acceleration
        Vec3 acceleration = Vec3.ZERO;
        acceleration = applyForce(acceleration, separation);
        acceleration = applyForce(acceleration, alignment);
        acceleration = applyForce(acceleration, cohesion);

        return acceleration;
    }

    Vec3 applyForce(Vec3 acceleration, Vec3 force)
    {
        //todo: add mass here (A = F / M)
        return acceleration.add(force);
    }

    Vec3 calculateSeparation(List<ISimulationObject> simulationObjects)
    {
        return Vec3.ZERO;
    }

    Vec3 calculateAlignment(List<ISimulationObject> simulationObjects)
    {
        return Vec3.ZERO;
    }

    Vec3 calculateCohesion(List<ISimulationObject> simulationObjects)
    {
        return Vec3.ZERO;
    }

}
