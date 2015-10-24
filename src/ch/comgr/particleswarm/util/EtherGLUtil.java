package ch.comgr.particleswarm.util;

import ch.fhnw.util.math.Vec3;

import java.util.Random;

/**
 * Created by cansik on 24/10/15.
 */
public class EtherGLUtil {
    public static final double TWO_PI = Math.PI * 2;

    /**
     * Limits the maximal value of a vector. Rescales the vector if one of the elements are over the limit.
     * @param v Vector to limit
     * @param max Max value of the elements
     * @return Limited vector
     */
    public static Vec3 limit(Vec3 v, float max)
    {
        if(v.length() > max*max)
        {
            v = v.normalize();
            v = v.scale(max);
        }

        return v;
    }

    /**
     * Checks if two vec3 are equals with real floating-point checks.
     * @param a First vector
     * @param b Second vector
     * @return True if vector a is equals vector b
     */
    public static boolean equalVec3(Vec3 a, Vec3 b)
    {
        return (isFloatingEqual(a.x, b.x)
                && isFloatingEqual(a.y, b.y)
                && isFloatingEqual(a.z, b.z));
    }


    /**
     * Compare to floats for (almost) equality. Will check whether they are
     * at most 5 ULP apart.
     * @param v1
     * @param v2
     * @return
     */
    public static boolean isFloatingEqual(float v1, float v2) {
        if (v1 == v2)
            return true;
        float absoluteDifference = Math.abs(v1 - v2);
        float maxUlp = Math.max(Math.ulp(v1), Math.ulp(v2));
        return absoluteDifference < 5 * maxUlp;
    }

    /**
     * Generates a random vec3 with a uniform spherical distribution.
     * @return Spherical distributed vec3
     */
    public static Vec3 randomVec3()
    {
        double phi = Math.random() * TWO_PI;
        double costheta = GetRandomFloat(-1, 1);
        double theta = Math.acos(costheta);

        double x = Math.sin(theta) * Math.cos(phi);
        double y = Math.sin(theta) * Math.sin(phi);
        double z = Math.cos(theta);

        return new Vec3((float)x, (float)y, (float)z);
    }

    /**
     * Generates a random float in a specific range.
     * @param lower Lower bound of the range
     * @param upper Upper bound of the range
     * @return Random floating point number
     */
    public static float GetRandomFloat(float lower, float upper) {
        return lower + new Random().nextFloat() * (upper - lower);
    }
}