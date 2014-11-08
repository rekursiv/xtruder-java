package com.protoplant.xtruder2;

public class StepperStatusEvent {

	private int speed;
	private int torque;
	private int status;

	
	public StepperStatusEvent(int speed, int torque, int status) {
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



}
