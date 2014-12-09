package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperType;

public class StepperStatusEvent {

	private StepperType type;
	private int speed;
	private int torque;
	private int status;

	
	public StepperStatusEvent(StepperType type, int speed, int torque, int status) {
		this.type = type;
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
	
	public StepperType getType() {
		return type;
	}



}
