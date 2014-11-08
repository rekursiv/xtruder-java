package com.protoplant.xtruder2;

public class StepperSpeedChangeEvent {

	private String serial;
	private int speed;

	public StepperSpeedChangeEvent(String serial, int speed) {
		this.serial = serial;
		this.setSpeed(speed);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getSerial() {
		return serial;
	}

}
