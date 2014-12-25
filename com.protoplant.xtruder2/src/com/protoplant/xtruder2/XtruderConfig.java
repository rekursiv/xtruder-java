package com.protoplant.xtruder2;


import com.google.inject.Singleton;

import util.config.ConfigBase;


public class XtruderConfig extends ConfigBase {

	// logging
	public boolean logToConsole=true;
	public boolean logToFile=false;

	
	public ConveyanceConfig conveyance = new ConveyanceConfig();
	
	public StepperConfig[] steppers;
	
	
}
