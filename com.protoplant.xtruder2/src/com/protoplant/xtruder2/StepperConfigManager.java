package com.protoplant.xtruder2;

import java.util.logging.Logger;

import util.config.ConfigManager;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/*
				stepMode;
				isGain;
				holdingTorque;
				minTorque;
				maxTorque;
				torqueDiv;
				accelDiv;
				accelStep;
 */

@Singleton
public class StepperConfigManager {

	private Logger log;
	
	protected final ConfigManager<StepperConfigList> cfgMgr = new ConfigManager<StepperConfigList>(StepperConfigList.class, "config/steppers.js");

	@Inject
	public StepperConfigManager(Logger log) {
		this.log = log;
		cfgMgr.load();
		
	}

	public StepperType getType(HIDDeviceInfo devInfo) {
		log.info(devInfo.getSerial_number());
		if (devInfo.getSerial_number().equals("71D1906F18002B00")) return StepperType.TopRoller;
		else return StepperType.BottomRoller;
	}
	
	

}
