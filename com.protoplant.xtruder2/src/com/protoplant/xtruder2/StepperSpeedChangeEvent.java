package com.protoplant.xtruder2;

public class StepperSpeedChangeEvent {

	private int speed;

	public StepperSpeedChangeEvent(int speed) {
		this.setSpeed(speed);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

}
