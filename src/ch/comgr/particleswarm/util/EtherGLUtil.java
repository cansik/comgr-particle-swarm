package ch.comgr.particleswarm.util;

import ch.fhnw.util.math.Vec3;

/**
 * Created by cansik on 24/10/15.
 */
public class EtherGLUtil {
    public static final double TWO_PI = Math.PI * 2;

    public static Vec3 limit(Vec3 v, float max)
    {
        if(v.length() > max*max)
        {
            v = v.normalize();
            v = v.scale(max);
        }

        return v;
    }

    public static boolean equalVec3(Vec3 a, Vec3 b)
    {
        return (isFloatingEqual(a.x, b.x)
                && isFloatingEqual(a.y, b.y)
                && isFloatingEqual(a.z, b.z));
    }

    /**
     * Compare to floats for (almost) equality. Will check whether they are
     * at most 5 ULP apart.
     */
    public static boolean isFloatingEqual(float v1, float v2) {
        if (v1 == v2)
            return true;
        float absoluteDifference = Math.abs(v1 - v2);
        float maxUlp = Math.max(Math.ulp(v1), Math.ulp(v2));
        return absoluteDifference < 5 * maxUlp;
    }
}
