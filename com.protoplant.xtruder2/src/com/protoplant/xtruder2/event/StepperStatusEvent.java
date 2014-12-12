package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperStatusEvent {

	private StepperFunction function;
	private int speed;
	private int torque;
	private int status;

	
	public StepperStatusEvent(StepperFunction type, int speed, int torque, int status) {
		this.function = type;
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
	
	public StepperFunction getFunction() {
		return function;
	}



}
