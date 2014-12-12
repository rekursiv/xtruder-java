package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperRunEvent {

	private StepperFunction function;

	public StepperRunEvent(StepperFunction function) {
		this.function = function;
	}

	public StepperFunction getFunction() {
		return function;
	}

}
