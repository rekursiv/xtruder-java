package com.protoplant.xtruder2.config;


import java.util.TreeMap;
import util.config.ConfigBase;
import com.protoplant.xtruder2.StepperFunction;


public class XtruderConfig extends ConfigBase {

	// logging
	public boolean logToConsole=true;
	public boolean logToFile=false;

	public ConveyanceConfig conveyance = new ConveyanceConfig();
	public FeedbackConfig feedback = new FeedbackConfig();
	public ConversionConfig conversion = new ConversionConfig();
	
	public TreeMap<StepperFunction, StepperConfig> steppers;
	

}
