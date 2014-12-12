package com.protoplant.xtruder2;

public class StepperConfig {
	
	public StepperFunction function;
	public String serial;
	
	public int stepMode = 4;
	public int isGain = 3;
	public int holdingTorque = 0;
	public int minTorque = 127;
	public int maxTorque = 255;
	public int torqueDiv = 255;
	public int accelDiv = 1;
	public int accelStep = 8;

	
	
	public StepperConfig() {
		this(StepperFunction.UNDEFINED);
	}
	
	public StepperConfig(StepperFunction function) {
		this.function = function;
	}

	

}
