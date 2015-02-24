package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperStatusEvent {

	private StepperFunction function;
	private int speed;
	private int torque;
	private int status;
	private int position;

	
	public StepperStatusEvent(StepperFunction type, int speed, int torque, int status, int position) {
		this.function = type;
		this.speed = speed;
		this.torque = torque;
		this.status = status;
		this.position = position;
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

	public int getPosition() {
		return position;
	}



}
