package com.protoplant.xtruder2.usb;

public class StepperUndefinedEvent {

	private String serial;
	
	public StepperUndefinedEvent(String serial) {
		this.serial = serial;
	}
	
	public String getSerial() {
		return serial;
	}

}
