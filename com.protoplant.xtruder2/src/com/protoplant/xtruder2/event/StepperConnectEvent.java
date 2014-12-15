package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperConnectEvent {

	private String serial;
	private StepperFunction function;
	
	public StepperConnectEvent(StepperFunction function, String serial) {
		this.function = function;
		this.serial = serial;
	}
	
	public StepperFunction getFunction() {
		return function;
	}
	
	public String getSerial() {
		return serial;
	}

}
