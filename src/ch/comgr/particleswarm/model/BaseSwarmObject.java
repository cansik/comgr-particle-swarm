package ch.comgr.particleswarm.model;

import ch.comgr.particleswarm.controller.SwarmSimulation;
import ch.comgr.particleswarm.util.EtherGLUtil;
import ch.comgr.particleswarm.util.Tuple;
import ch.comgr.particleswarm.util.UpdateEventArgs;
import ch.fhnw.ether.scene.I3DObject;
import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import static ch.comgr.particleswarm.util.EtherGLUtil.equalVec3;

/**
 * Created by cansik on 24/10/15.
 */
public abstract class BaseSwarmObject extends BaseObject {

    private UpdateEventArgs args;

    float angle;
    Vec3 velocity;

    private List<BaseSwarmObject> swarm;
    private List<Tuple<BaseSwarmObject, Float>> neighbours;

    /**
     * Creates a new Swarm Object which try's to align to other objects.
     */
    public BaseSwarmObject(Vec3 startPosition, SwarmSimulation swarmSimulation) {
        super(startPosition, swarmSimulation);

        //get random spawn angle
        angle = (float) (Math.random() * EtherGLUtil.TWO_PI);

        //set velocity to initial amount
        velocity = EtherGLUtil.randomVec3();
    }

    /**
     * Performs next step for this Swarm Object and updates position and velocity.
     *
     * @param args Simulation arguments.
     */
    public void nextStep(UpdateEventArgs args) {
        //set arguments
        this.args = args;

        //get swarm
        swarm = new ArrayList<>();

        for (ISimulationObject sim : args.getSimulationObjects())
            if (sim instanceof BaseSwarmObject)
                swarm.add((BaseSwarmObject) sim);

        //get neighbours
        neighbours = getNeighbours(args.getNeighbourRadius());

        //calculate new acceleration
        Vec3 acceleration = calculateAcceleration();

        //apply acceleration to velocity and count position
        velocity = velocity.add(acceleration);
        velocity = EtherGLUtil.limit(velocity, args.getMaxSpeed());

        setPosition(getPosition().add(velocity));
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
        separation = separation.scale(args.getSeparationWeight());
        alignment = alignment.scale(args.getAlignmentWeight());
        cohesion = cohesion.scale(args.getCohesionWeight());

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

        Vec3 position = getPosition();
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

        if (x > args.getBoxWidth() + r) v1 *= -1;
        if (y > args.getBoxHeight() + r) v2 *= -1;
        if (z > args.getBoxDepth() + r) v3 *= -1;

        velocity = new Vec3(v1, v2, v3);
    }

    /**
     * Checks if object is out of bounds and puts in on the other side of the box.
     */
    void checkBordersAndGoThrough() {
        //rand_abstand
        float r = 0f;

        Vec3 position = getPosition();
        float x = position.x;
        float y = position.y;
        float z = position.z;

        if (x < -r) x = args.getBoxWidth() + r;
        if (y < -r) y = args.getBoxHeight() + r;
        if (z < -r) z = args.getBoxDepth() + r;

        if (x > args.getBoxWidth() + r) x = -r;
        if (y > args.getBoxHeight() + r) y = -r;
        if (z > args.getBoxDepth() + r) z = -r;

        setPosition(new Vec3(x, y, z));
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
        Vec3 desired = target.subtract(getPosition());

        desired = desired.normalize();
        desired = desired.scale(args.getMaxSpeed());

        Vec3 steer = desired.subtract(velocity);
        steer = EtherGLUtil.limit(steer, args.getMaxForce());

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
            if (n.getSecond() > args.getDesiredSeparation())
                continue;

            Vec3 diff = getPosition().subtract(n.getFirst().getPosition());

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
                    .scale(args.getMaxSpeed())
                    .subtract(velocity);

            direction = EtherGLUtil.limit(direction, args.getMaxForce());
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
                .scale(args.getMaxSpeed())
                .subtract(velocity);

        direction = EtherGLUtil.limit(direction, args.getMaxForce());

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
            direction = direction.add(n.getFirst().getPosition());

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

    public Vec3 getPosition() {
        return super.getPosition();
    }

    /**
     * Returns the distance to the other BaseSwarmObject
     *
     * @param bso To measure the distance
     * @return Distnace to the other BaseSwarmObject
     */
    public float getDistanceTo(BaseSwarmObject bso) {
        return Math.abs(getPosition().distance(bso.getPosition()));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BaseSwarmObject))
            return false;

        BaseSwarmObject bso = (BaseSwarmObject) obj;

        boolean res = bso.getPosition().equals(getPosition()) && bso.velocity.equals(this.velocity);
        boolean res2 = equalVec3(bso.getPosition(), getPosition()) && equalVec3(bso.velocity, velocity);

        assert res == res2;

        return res2;
    }
}
