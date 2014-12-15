package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.StepperFunction;

public class StepperDisconnectEvent {

	private StepperFunction function;

	public StepperDisconnectEvent(StepperFunction function) {
		this.function = function;
	}

	public StepperFunction getFunction() {
		return function;
	}

}
