package com.protoplant.xtruder2;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.protoplant.xtruder2.config.XtruderConfig;

@Singleton
public class ConversionManager {

	private Logger log;
	private XtruderConfig config;

	@Inject
	public ConversionManager(Logger log, XtruderConfig config) {
		this.log = log;
		this.config = config;
	}
	
	
//  convert speed from "native stepper units" to "inches per second"
	public float toIps(int stepperSpeed) {
		int speed = Math.abs(stepperSpeed);
		int microStepsPerRev = config.conversion.stepsPerRev*config.conversion.microStepsPerStep;
		float rollerCircumference = config.conversion.rollerDiameter*(float)Math.PI;
		return ((float)speed/(float)microStepsPerRev)*rollerCircumference;
	}


	public float toPsi(int pressure) {
		return (float)pressure*config.conversion.pressureScale;
	}
	
	
	
}
