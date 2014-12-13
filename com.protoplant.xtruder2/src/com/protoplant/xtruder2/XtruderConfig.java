package com.protoplant.xtruder2;


import com.google.inject.Singleton;

import util.config.ConfigBase;


//@Singleton
public class XtruderConfig extends ConfigBase {

	// logging
	public boolean logToConsole=true;
	public boolean logToFile=false;
	public boolean showLogView=true;   //   FIXME
 	
	public int test = 0;
	
	public StepperConfig[] steppers;// = {new StepperConfig(), new StepperConfig()};
	
//	public StepperConfig[] steppers = {new StepperConfig("one"), new StepperConfig("two")};
}
