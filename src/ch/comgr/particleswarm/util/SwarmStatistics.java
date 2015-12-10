package ch.comgr.particleswarm.util;

public class SwarmStatistics {
    public volatile int NumOfMeshes = 0;
    public volatile float NumOfObjects = 0; // todo: change to int ?
    public final FPSCounter FpsCounter = new FPSCounter();
}
