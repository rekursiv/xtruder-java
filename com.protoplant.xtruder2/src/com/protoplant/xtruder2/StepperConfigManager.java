package com.protoplant.xtruder2;

import java.util.logging.Logger;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StepperConfigManager {

	private Logger log;

	@Inject
	public StepperConfigManager(Logger log) {
		this.log = log;
	}

	public StepperType getType(HIDDeviceInfo devInfo) {
		log.info(devInfo.getSerial_number());
		if (devInfo.getSerial_number().equals("0ED1906F1D000500")) return StepperType.ONE;
		else return StepperType.TWO;
	}
	
	

}
