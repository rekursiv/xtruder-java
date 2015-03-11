package com.protoplant.xtruder2.config;


public class StepperState {
	
	public int speedSliderInit = 4000;
	public boolean isTracking = false;
	public float trackingScaleFactor = 1.0f;
	
	
	public StepperState(int speedSliderInit) {
		this.speedSliderInit = speedSliderInit;
	}
	
	public StepperState(int speedSliderInit, boolean isTracking, float trackingScaleFactor) {
		this.speedSliderInit = speedSliderInit;
		this.isTracking = isTracking;
		this.trackingScaleFactor = trackingScaleFactor;
	}
	
}
