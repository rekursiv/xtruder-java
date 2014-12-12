package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperSpeedChangeEvent {

	private StepperFunction function;
	private int speed;

	public StepperSpeedChangeEvent(StepperFunction function, int speed) {
		this.function = function;
		this.setSpeed(speed);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public StepperFunction getFunction() {
		return function;
	}

}
