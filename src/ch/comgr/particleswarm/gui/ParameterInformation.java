package ch.comgr.particleswarm.gui;

public class ParameterInformation{
    public final StringToParameterConverter Converter;
    public final Parameters Parameter;
    public Object Value;

    public ParameterInformation(Parameters parameter, Object defaultValue, StringToParameterConverter converter) {
        Value = defaultValue;
        Converter = converter;
        Parameter = parameter;
    }
}