package com.protoplant.xtruder2;



import util.config.ConfigBase;

public class XtruderConfig extends ConfigBase {

	// logging
	public boolean logToConsole=true;
	public boolean logToFile=false;
	public boolean showLogView=true;
	
	public StepperConfig[] motors = {new StepperConfig()};
	
}
