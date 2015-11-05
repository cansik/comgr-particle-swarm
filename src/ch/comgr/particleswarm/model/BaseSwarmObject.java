package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.Tuple;
import ch.fhnw.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.comgr.particleswarm.util.EtherGLUtil.equalVec3;

/**
 * Created by cansik on 24/10/15.
 */
public abstract class BaseSwarmObject {

    //todo: change this default values in the UI
    private static final float MAX_SPEED = 0.5f;
    private static final float MAX_FORCE = 0.03f;

    private static final float SEPARATION_WEIGHT = 1.5f;
    private static final float ALIGNMENT_WEIGHT = 1.0f;
    private static final float COHESION_WEIGHT = 1.0f;

    private static final float DESIRED_SEPARATION = 5f;
    public static final float NEIGHBOUR_RADIUS = 15f;

    private static final float BOX_WIDTH = 100;
    private static final float BOX_HEIGHT = 100;
    private static final float BOX_DEPTH = 100;

    float angle;
    Vec3 position;
    Vec3 velocity;

    private List<BaseSwarmObject> swarm;
    private List<Tuple<BaseSwarmObject, Float>> neighbours;

    /**
     * Creates a new Swarm Object which try's to align to other objects.
     */
    public BaseSwarmObject(Vec3 startPosition) {
        //get random spawn angle
        angle = (float) (Math.random() * EtherGLUtil.TWO_PI);

        //start from position zero
        position = startPosition;

        //set velocity to initial amount
        velocity = EtherGLUtil.randomVec3();
    }

    /**
     * Performs next step for this Swarm Object and updates position and velocity.
     *
     * @param simulationObjects List of objects in the simulation
     */
    public void nextStep(List<ISimulationObject> simulationObjects) {
        //todo: check if lambda is fast enough!
        //get swarm
        swarm = new ArrayList<>();

        for (ISimulationObject sim : simulationObjects)
            if (sim instanceof BaseSwarmObject)
                swarm.add((BaseSwarmObject) sim);

        //get neighbours
        neighbours = getNeighbours(NEIGHBOUR_RADIUS);

        //calculate new acceleration
        Vec3 acceleration = calculateAcceleration();

        //apply acceleration to velocity and count position
        velocity = velocity.add(acceleration);
        velocity = EtherGLUtil.limit(velocity, MAX_SPEED);

        position = position.add(velocity);
        checkBorders();
    }

    /**
     * Calculates the acceleration for this cycle.
     *
     * @return Current acceleration
     */
    Vec3 calculateAcceleration() {
        //calculate forces
        Vec3 separation = calculateSeparation();
        Vec3 alignment = calculateAlignment();
        Vec3 cohesion = calculateCohesion();

        //weight the forces
        separation = separation.scale(SEPARATION_WEIGHT);
        alignment = alignment.scale(ALIGNMENT_WEIGHT);
        cohesion = cohesion.scale(COHESION_WEIGHT);

        //generate acceleration
        Vec3 acceleration = Vec3.ZERO;
        acceleration = applyForce(acceleration, separation);
        acceleration = applyForce(acceleration, alignment);
        acceleration = applyForce(acceleration, cohesion);

        return acceleration;
    }


    void checkBorders() {
        //rand_abstand
        float r = 2f;

        float x = position.x;
        float y = position.y;
        float z = position.z;

        float v1 = velocity.x;
        float v2 = velocity.y;
        float v3 = velocity.z;

        //lower
        if (x < -r) v1 *= -1;
        if (y < -r) v2 *= -1;
        if (z < -r) v3 *= -1;

        if (x > BOX_WIDTH + r) v1 *= -1;
        if (y > BOX_HEIGHT + r) v2 *= -1;
        if (z > BOX_DEPTH + r) v3 *= -1;

        velocity = new Vec3(v1, v2, v3);
    }

    /**
     * Checks if object is out of bounds and puts in on the other side of the box.
     */
    void checkBordersAndGoThrough() {
        //rand_abstand
        float r = 0f;

        float x = position.x;
        float y = position.y;
        float z = position.z;

        if (x < -r) x = BOX_WIDTH + r;
        if (y < -r) y = BOX_HEIGHT + r;
        if (z < -r) z = BOX_DEPTH + r;

        if (x > BOX_WIDTH + r) x = -r;
        if (y > BOX_HEIGHT + r) y = -r;
        if (z > BOX_DEPTH + r) z = -r;

        position = new Vec3(x, y, z);
    }

    /**
     * Applies force to a given acceleration.
     *
     * @param acceleration Given acceleration
     * @param force        Force to add
     * @return Acceleration with force
     */
    Vec3 applyForce(Vec3 acceleration, Vec3 force) {
        //todo: add mass here (A = F / M)
        return acceleration.add(force);
    }

    /**
     * Calculates the force to steer to a target location.
     *
     * @param target Location where to steer at
     * @return Force to target location
     */
    Vec3 getSteerToTargetForce(Vec3 target) {
        Vec3 desired = target.subtract(position);

        desired = desired.normalize();
        desired = desired.scale(MAX_SPEED);

        Vec3 steer = desired.subtract(velocity);
        steer = EtherGLUtil.limit(steer, MAX_FORCE);

        return steer;
    }

    /**
     * Calculates the avoidens location for the current Swarm Object.
     *
     * @return Force to the avoidens location
     */
    Vec3 calculateSeparation() {
        Vec3 direction = Vec3.ZERO;

        if (neighbours.size() == 0)
            return direction;

        int neighbourCount = 0;

        for (Tuple<BaseSwarmObject, Float> n : neighbours) {
            if (n.getSecond() > DESIRED_SEPARATION)
                continue;

            Vec3 diff = position.subtract(n.getFirst().position);

            diff = diff.normalize();
            //weight by distance
            diff = diff.scale(1.0f / n.getSecond());

            direction = direction.add(diff);
            neighbourCount++;
        }

        if (neighbourCount == 0)
            return direction;

        if (direction.length() > 0) {
            direction = direction.scale(1.0f / neighbourCount)
                    .normalize()
                    .scale(MAX_SPEED)
                    .subtract(velocity);

            direction = EtherGLUtil.limit(direction, MAX_FORCE);
        }
        return direction;
    }

    /**
     * Calculates the average velocity of all neighbours.
     *
     * @return Average velocity of all neighbours
     */
    Vec3 calculateAlignment() {
        Vec3 direction = Vec3.ZERO;

        if (neighbours.size() == 0)
            return direction;

        for (Tuple<BaseSwarmObject, Float> n : neighbours)
            direction = direction.add(n.getFirst().velocity);

        direction = direction.scale(1.0f / neighbours.size())
                .normalize()
                .scale(MAX_SPEED)
                .subtract(velocity);

        direction = EtherGLUtil.limit(direction, MAX_FORCE);

        return direction;
    }

    /**
     * Calculates the average position of all neighbours.
     *
     * @return Force to the average position of all neighbours
     */
    Vec3 calculateCohesion() {
        Vec3 direction = Vec3.ZERO;

        if (neighbours.size() == 0)
            return direction;

        for (Tuple<BaseSwarmObject, Float> n : neighbours)
            direction = direction.add(n.getFirst().position);

        direction = direction.scale(1.0f / neighbours.size());
        return getSteerToTargetForce(direction);
    }

    /**
     * Returns the BaseSwarmObjects in the near
     *
     * @param maximalDistance Maximum Distnace BaseSwarmObject <= will be returned
     * @return List of tupel with BaseSwarmObject and distance
     */
    public List<Tuple<BaseSwarmObject, Float>> getNeighbours(float maximalDistance) {
        List<Tuple<BaseSwarmObject, Float>> n = new ArrayList<>();

        for (BaseSwarmObject b : swarm) {
            if (b.equals(this))
                continue;

            float distance = b.getDistanceTo(this);

            if (distance <= maximalDistance)
                n.add(new Tuple<>(b, distance));
        }

        return n;
    }

    /**
     * Returns the distance to the other BaseSwarmObject
     *
     * @param bso To measure the distance
     * @return Distnace to the other BaseSwarmObject
     */
    public float getDistanceTo(BaseSwarmObject bso) {
        return Math.abs(position.distance(bso.position));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BaseSwarmObject))
            return false;

        BaseSwarmObject bso = (BaseSwarmObject) obj;

        boolean res = bso.position.equals(this.position) && bso.velocity.equals(this.velocity);
        boolean res2 = equalVec3(bso.position, position) && equalVec3(bso.velocity, velocity);

        assert res == res2;

        return res2;
    }
}
