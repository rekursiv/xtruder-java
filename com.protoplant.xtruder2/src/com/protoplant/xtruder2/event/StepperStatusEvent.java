package com.protoplant.xtruder2.event;

public class StepperStatusEvent {

	private String serial;
	private int speed;
	private int torque;
	private int status;

	
	public StepperStatusEvent(String serial, int speed, int torque, int status) {
		this.serial = serial;
		this.speed = speed;
		this.torque = torque;
		this.status = status;
	}
	
	public int getSpeed() {
		return speed;
	}

	public int getTorque() {
		return torque;
	}

	public int getStatus() {
		return status;
	}
	
	public String getSerial() {
		return serial;
	}



}
