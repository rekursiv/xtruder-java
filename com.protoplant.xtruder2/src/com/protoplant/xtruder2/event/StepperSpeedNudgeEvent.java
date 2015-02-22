package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperSpeedNudgeEvent {

	private StepperFunction function;
	private int delta;

	public StepperSpeedNudgeEvent(StepperFunction function, int delta) {
		this.function = function;
		this.setDelta(delta);
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

	public StepperFunction getFunction() {
		return function;
	}

}
