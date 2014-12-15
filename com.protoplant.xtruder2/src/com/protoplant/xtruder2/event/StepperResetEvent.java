package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperResetEvent {

	private StepperFunction function;

	public StepperResetEvent(StepperFunction function) {
		this.function = function;
	}

	public StepperFunction getFunction() {
		return function;
	}

}
