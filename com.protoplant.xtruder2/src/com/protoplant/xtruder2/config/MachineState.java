package com.protoplant.xtruder2.config;


import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.protoplant.xtruder2.StepperFunction;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MachineState {

	public ConversionState conversion = new ConversionState();
	public FeedbackState feedback = new FeedbackState();
	public ConveyanceState conveyance = new ConveyanceState();
	public TreeMap<StepperFunction, StepperState> steppers = new TreeMap<StepperFunction, StepperState>();
	

}
