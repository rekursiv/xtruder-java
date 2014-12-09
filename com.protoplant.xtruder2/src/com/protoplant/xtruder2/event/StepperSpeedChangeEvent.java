package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperType;

public class StepperSpeedChangeEvent {

	private StepperType type;
	private int speed;

	public StepperSpeedChangeEvent(StepperType type, int speed) {
		this.type = type;
		this.setSpeed(speed);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public StepperType getType() {
		return type;
	}

}
