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
}
