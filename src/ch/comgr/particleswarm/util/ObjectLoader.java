package ch.comgr.particleswarm.util;

import ch.fhnw.ether.formats.obj.OBJReader;
import ch.fhnw.ether.scene.mesh.IMesh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roger on 23.10.2015.
 */
public class ObjectLoader {
    /**
     * Loads all meshes from an object file.
     *
     * @param path Path to the object file
     * @return List of meshes of the object file
     */
    public List<IMesh> getMeshesFromObject(String path) {
        try {
            return new OBJReader(getClass().getResource(path)).getMeshes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
