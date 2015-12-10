package ch.comgr.particleswarm.util;

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


    public final FloatProperty NewNumerOfObjects = new FloatProperty("NewNumerOfObjects", Color.green, 15, 1000);
    public final FloatProperty NeighbourRadius = new FloatProperty("Neighbour Radius", Color.red, 15.0f, 30f);
    public final FloatProperty Separation = new FloatProperty("Separation", Color.orange, 1.5f, 5f);
    public final FloatProperty Alignment = new FloatProperty("Alignment", Color.orange, 1.0f, 5f);
    public final FloatProperty Cohesion = new FloatProperty("Cohesion", Color.orange, 1.0f, 5f);
    public final FloatProperty MaxSpeed = new FloatProperty("MaxSpeed", Color.yellow, 0.5f, 2f);
    public final FloatProperty MaxForce = new FloatProperty("MaxForce", Color.yellow, 0.03f, 2f);
    public final FloatProperty DesSeparation = new FloatProperty("Des-Separation", Color.red, 5.0f, 30f);
    public final FloatProperty BoxWidth = new FloatProperty("BoxWidth", 100f);
    public final FloatProperty BoxHeight = new FloatProperty("BoxHeight", 100f);
    public final FloatProperty BoxDepth = new FloatProperty("BoxDepth", 100f);

    //TODO: RRA - Adding boolean Property (extracting FloatProperty-Class)
    public volatile boolean DebugMode = false;
}
