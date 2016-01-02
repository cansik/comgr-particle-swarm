package ch.comgr.particleswarm.util;

import ch.comgr.particleswarm.model.BaseSwarmObject;
import ch.fhnw.util.math.Vec3;

import java.util.*;

public class GridCoordination {
    private final int limitX;
    private final int limitY;
    private final int limitZ;

    private final Set<BaseSwarmObject>[] xSwarm;
    private final Set<BaseSwarmObject>[] ySwarm;
    private final Set<BaseSwarmObject>[] zSwarm;

    public GridCoordination(int limitX, int limitY, int limitZ) {
        this.limitX = limitX;
        this.limitY = limitY;
        this.limitZ = limitZ;

        xSwarm = new HashSet[limitX];
        ySwarm = new HashSet[limitY];
        zSwarm = new HashSet[limitZ];
    }

    public void addMesh(BaseSwarmObject swarm){
        Vec3 position = swarm.getPosition();
        initIfNecessaryAndAdd(xSwarm, getValueIncllimit(position.x, 0, limitX-1), swarm);
        initIfNecessaryAndAdd(ySwarm, getValueIncllimit(position.y, 0, limitY-1), swarm);
        initIfNecessaryAndAdd(zSwarm, getValueIncllimit(position.z, 0, limitZ-1), swarm);
    }

    public List<BaseSwarmObject> getSwarmsInRadius(Vec3 referencePosition, int radius){
        int startX = Math.max(0, (int) referencePosition.x - radius);
        int startY = Math.max(0, (int) referencePosition.y - radius);
        int startZ = Math.max(0, (int) referencePosition.z - radius);

        int endX = Math.min(limitX - 1, (int) referencePosition.x + radius);
        int endY = Math.min(limitY - 1, (int) referencePosition.y + radius);
        int endZ = Math.min(limitZ - 1, (int) referencePosition.z + radius);

        // intersection
        Set<BaseSwarmObject> result = getSwarmsInCoordsX(startX, endX);
        result.retainAll(getSwarmsInCoordsY(startY, endY));
        result.retainAll(getSwarmsInCoordsZ(startZ, endZ));

        return new ArrayList<>(result);
    }

    private int getValueIncllimit(float value, int min, int max){
        return limit((int)value, min, max);
    }

    private int limit(int value, int min, int max){
        int valueLimitedToMinimum = Math.max(min, value);
        return Math.min(valueLimitedToMinimum, max); // value limited to maximum
    }

    private void initIfNecessaryAndAdd(Set<BaseSwarmObject>[] swarmSetForCoordinate, int index, BaseSwarmObject swarm){
        if(swarmSetForCoordinate[index] == null){
            swarmSetForCoordinate[index] = new HashSet<BaseSwarmObject>();
        }
        swarmSetForCoordinate[index].add(swarm);
    }

    private Set<BaseSwarmObject> getSwarmsInCoordsX(int startX, int endX){
        HashSet<BaseSwarmObject> result = new HashSet<BaseSwarmObject>();
        for(int i = startX; i <= endX; i++){
            if(xSwarm[i] != null){
                result.addAll(xSwarm[i]);
            }
        }
        return result;
    }

    private Set<BaseSwarmObject> getSwarmsInCoordsY(int startY, int endY){
        HashSet<BaseSwarmObject> result = new HashSet<BaseSwarmObject>();
        for(int i = startY; i <= endY; i++){
            if(ySwarm[i] != null){
                result.addAll(ySwarm[i]);
            }
        }
        return result;
    }


    private Set<BaseSwarmObject> getSwarmsInCoordsZ(int startZ, int endZ){
        HashSet<BaseSwarmObject> result = new HashSet<BaseSwarmObject>();
        for(int i = startZ; i <= endZ; i++){
            if(zSwarm[i] != null){
                result.addAll(zSwarm[i]);
            }
        }
        return result;
    }
}
