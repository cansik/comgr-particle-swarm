package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.Tuple;
import ch.fhnw.util.math.Vec3;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cansik on 24/10/15.
 */
public abstract class BaseSwarmObject {

    //todo: change this default values in the UI
    private static final float MAX_SPEED = 2.0f;
    private static final float MAX_FORCE = 0.03f;

    private static final float SPEARATION_WEIGHT = 1.5f;
    private static final float ALIGNMENT_WEIGHT = 1.0f;
    private static final float COHESION_WEIGHT = 1.0f;

    private final float DESIRED_SEPARATION = 25f;
    private static final float NEIGHBOUR_RADIUS = 50f;

    float angle;
    Vec3 position;
    Vec3 velocity;

    private List<BaseSwarmObject> swarm;
    private List<Tuple<BaseSwarmObject, Float>> neighbours;

    /**
     * Creates a new Swarm Object which try's to align to other objects.
     */
    public BaseSwarmObject()
    {
        //get random spawn angle
        angle = (float)(Math.random()* EtherGLUtil.TWO_PI);

        //start from position zero
        position = Vec3.ZERO;

        //set velocity to initial amount
        velocity = new Vec3((float)Math.cos(angle), (float)Math.sin(angle), (float)Math.tan(angle));
    }

    /**
     * Performs next step for this Swarm Object and updates position and velocity.
     * @param simulationObjects List of objects in the simulation
     */
    public void nextStep(List<ISimulationObject> simulationObjects)
    {
        //todo: check if lambda is fast!
        //get swarm
        swarm = simulationObjects.stream()
                .map(so -> (so instanceof BaseSwarmObject) ? (BaseSwarmObject) so : null).filter(bso -> bso != null)
                .collect(Collectors.toList());

        //get neighbours
        neighbours = getNeighbours(NEIGHBOUR_RADIUS);

        //calculate new acceleration
        Vec3 acceleration = calculateAcceleration();

        //apply acceleration to velocity and update position
        velocity = velocity.add(acceleration);
        velocity = EtherGLUtil.limit(velocity, MAX_SPEED);

        position = position.add(velocity);
    }

    /**
     * Calculates the acceleration for this cycle.
     * @return Current acceleration
     */
    Vec3 calculateAcceleration()
    {
        //calculate forces
        Vec3 separation = calculateSeparation();
        Vec3 alignment = calculateAlignment();
        Vec3 cohesion = calculateCohesion();

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

    /**
     * Applies force to a given acceleration.
     * @param acceleration Given acceleration
     * @param force Force to add
     * @return Acceleration with force
     */
    Vec3 applyForce(Vec3 acceleration, Vec3 force)
    {
        //todo: add mass here (A = F / M)
        return acceleration.add(force);
    }

    /**
     * Calculates the force to steer to a target location.
     * @param target Location where to steer at
     * @return Force to target location
     */
    Vec3 getSteerToTargetForce(Vec3 target)
    {
        Vec3 desired = target.subtract(position);
        float distance = position.distance(target);

        desired = desired.normalize();
        desired = desired.scale(MAX_SPEED * (distance / 100));

        Vec3 steer = desired.subtract(velocity);
        steer = EtherGLUtil.limit(steer, MAX_FORCE);

        return steer;
    }

    /**
     * Calculates the avoidens location for the current Swarm Object.
     * @return Force to the avoidens location
     */
    Vec3 calculateSeparation()
    {
        Vec3 direction = Vec3.ZERO;

        if(neighbours.size() == 0)
            return direction;

        int neighbourCount = 0;

        for(Tuple<BaseSwarmObject, Float> n : neighbours) {
            if(n.getSecond() > DESIRED_SEPARATION)
                continue;

            Vec3 diff = position.subtract(n.getFirst().position);

            //weight by distance
            diff = diff.normalize().scale(1/n.getSecond());
            direction = direction.add(diff);
            neighbourCount++;
        }

        direction = direction.scale(1/neighbourCount)
                .normalize()
                .scale(MAX_SPEED)
                .subtract(velocity);

        direction = EtherGLUtil.limit(direction, MAX_FORCE);
        return direction;
    }

    /**
     * Calculates the average velocity of all neighbours.
     * @return Average velocity of all neighbours
     */
    Vec3 calculateAlignment()
    {
        Vec3 direction = Vec3.ZERO;

        if(neighbours.size() == 0)
            return direction;

        for(Tuple<BaseSwarmObject, Float> n : neighbours)
            direction = direction.add(n.getFirst().velocity);

        direction = direction.scale(1/neighbours.size())
                .normalize()
                .scale(MAX_SPEED)
                .subtract(velocity);

        direction = EtherGLUtil.limit(direction, MAX_FORCE);

        return direction;
    }

    /**
     * Calculates the average position of all neighbours.
     * @return Force to the average position of all neighbours
     */
    Vec3 calculateCohesion()
    {
        Vec3 direction = Vec3.ZERO;

        if(neighbours.size() == 0)
            return direction;

        for(Tuple<BaseSwarmObject, Float> n : neighbours)
            direction = direction.add(n.getFirst().position);

        direction = direction.scale(1/neighbours.size());
        return getSteerToTargetForce(direction);
    }

    /**
     * Returns the BaseSwarmObjects in the near
     * @param maximalDistance Maximum Distnace BaseSwarmObject <= will be returned
     * @return List of tupel with BaseSwarmObject and distance
     */
    public List<Tuple<BaseSwarmObject, Float>> getNeighbours(float maximalDistance)
    {
        return swarm.stream()
                .filter(b -> !b.equals(this))
                .map(b -> new Tuple<>(b, b.getDistanceTo(this)))
                .filter(t -> t.getSecond() <= maximalDistance)
                .collect(Collectors.toList());
    }

    /**
     * Returns the distance to the other BaseSwarmObject
     * @param bso To measure the distance
     * @return Distnace to the other BaseSwarmObject
     */
    public float getDistanceTo(BaseSwarmObject bso)
    {
        return position.distance(bso.position);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BaseSwarmObject))
            return false;

        BaseSwarmObject bso = (BaseSwarmObject)obj;

        return bso.position.equals(this.position) && bso.velocity.equals(this.velocity);
    }
}
