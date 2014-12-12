package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperStopEvent {

	private StepperFunction function;

	public StepperStopEvent(StepperFunction function) {
		this.function = function;
	}

	public StepperFunction getFunction() {
		return function;
	}

}
