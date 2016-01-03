package ch.comgr.particleswarm.util;

import ch.fhnw.util.math.Vec3;

import java.awt.*;

public class SwarmConfiguration
{
    public static class FloatProperty {
        private volatile float currentValue;
        private volatile float maxValue;
        public final String Name;
        public final Color Color;

        public FloatProperty(String name, float defaultValue){
            this(name, null, defaultValue, defaultValue * 5);
        }

        public FloatProperty(String name, Color color, float defaultValue, float maxValue){
            this.currentValue = defaultValue;
            this.maxValue = maxValue;
            this.Name = name;
            this.Color = (color != null) ? color : java.awt.Color.RED;
        }

        public float getCurrentValue(){ return currentValue;}
        public void setCurrentValue(float v){ currentValue = v;}
        public float getMaxValue() {return maxValue;}
    }

    public final FloatProperty NewNumberOfObjects = new FloatProperty("Objects", Color.green, 3, 1000);
    public final FloatProperty NeighbourRadius = new FloatProperty("Radius", Color.red, 15.0f, 30f);
    public final FloatProperty Separation = new FloatProperty("Separation", Color.orange, 1.5f, 5f);
    public final FloatProperty Alignment = new FloatProperty("Alignment", Color.orange, 1.0f, 5f);
    public final FloatProperty Cohesion = new FloatProperty("Cohesion", Color.orange, 1.0f, 5f);
    public final FloatProperty MaxSpeed = new FloatProperty("MaxSpeed", Color.yellow, 0.5f, 2f);
    public final FloatProperty MaxForce = new FloatProperty("MaxForce", Color.yellow, 0.03f, 2f);
    public final FloatProperty DesSeparation = new FloatProperty("Des. Separation", Color.red, 5.0f, 30f);
    public final FloatProperty BoxWidth = new FloatProperty("BoxWidth", 100f);
    public final FloatProperty BoxHeight = new FloatProperty("BoxHeight", 100f);
    public final FloatProperty BoxDepth = new FloatProperty("BoxDepth", 100f);
    public final FloatProperty MiscObject = new FloatProperty("MiscObject", 2f);

    //TODO: RRA - Adding boolean Property (extracting FloatProperty-Class)
    public volatile boolean DebugMode = false;
    public volatile boolean ShowController = true;

    public final double LoopInterval = 0.5; // 1.0/60.0
    public final float IncrementXy = 5f;
    public final float IncrementZ = 5f;
    public final int ScreenPositionX = 10;
    public final int ScreenPositionY = 40;
    public final int ScreenWidth = 1024;
    public final int ScreenHeight = 800;
    public final Vec3 DefaultCameraLocation = new Vec3(200, 200, 100);
}
