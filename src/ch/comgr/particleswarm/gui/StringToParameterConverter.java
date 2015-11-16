package ch.comgr.particleswarm.gui;

@FunctionalInterface
public interface StringToParameterConverter{
    Object convert(String s);
}
