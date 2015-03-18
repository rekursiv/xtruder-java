package com.protoplant.xtruder2.config;


public class StepperConfig {
	
	public String serial;
	
	// GUI settings
	public int speedSliderMin = 1000;
	public int speedSliderInit = 4000;
	public int speedSliderMax = 16000;
	public boolean isReversed = false;
	public boolean isTracking = false;
	public float trackingScaleFactor = 1.0f;
	
	// hardware module settings
	public int stepMode = 4;
	public int isGain = 3;
	public int holdingTorque = 0;
	public int minTorque = 127;
	public int maxTorque = 255;
	public int torqueDiv = 255;
	public int accelDiv = 1;
	public int accelStep = 1;
	public int loPos = 100;
	public int hiPos = 1000;
	public int posCountDiv = 0;   // set to "0" to disable "flip flop mode"


}
