package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperConfigChangeEvent {

	private StepperFunction function;

	public StepperConfigChangeEvent(StepperFunction function) {
		this.function = function;
	}

	public StepperFunction getFunction() {
		return function;
	}

}
