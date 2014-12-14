package com.protoplant.xtruder2;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import util.config.ConfigManager;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StepperConfigManager {

	private Logger log;
	private XtruderConfig config;
	private ConfigManager<XtruderConfig> cfgMgr;
	private Map<String, Integer> stepperSerialMap;
	private Map<StepperFunction, Integer> stepperFunctionMap;

	@Inject
	public StepperConfigManager(Logger log, XtruderConfig config, ConfigManager<XtruderConfig> cfgMgr) {
		this.log = log;
		this.config = config;
		this.cfgMgr = cfgMgr;
		initConfig();
	}

	private void initConfig() {
		if (config.steppers==null) {
			int numMotors = StepperFunction.values().length-1;  // last enum = "UNDEFINED" 
			config.steppers = new StepperConfig[numMotors];
			for (int i=0; i<numMotors; ++i) {
				config.steppers[i] = new StepperConfig(StepperFunction.values()[i]);
			}
			try {
				cfgMgr.save(config);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		buildStepperMaps();
	}
	
	public void buildStepperMaps() {
		stepperSerialMap = new HashMap<String, Integer>();
		stepperFunctionMap = new HashMap<StepperFunction, Integer>();	
		for (int i=0; i<config.steppers.length; ++i) {
			stepperSerialMap.put(config.steppers[i].serial, i);
			stepperFunctionMap.put(config.steppers[i].function, i);
		}
	}
	
	public StepperConfig getConfig(String serial) {
		return config.steppers[stepperSerialMap.get(serial)];
	}
	
	public StepperConfig getConfig(StepperFunction function) {
		return config.steppers[stepperFunctionMap.get(function)];
	}

	public StepperFunction getFunction(HIDDeviceInfo devInfo) {
		StepperConfig sc = getConfig(devInfo.getSerial_number());
		if (sc==null) {
			return StepperFunction.UNDEFINED;
		} else {
			return sc.function;
		}

		
//		log.info(devInfo.getSerial_number());
//		if (devInfo.getSerial_number().equals("71D1906F18002B00")) return StepperFunction.TopRoller;
//		else return StepperFunction.BottomRoller;
	}
	
/*
	private void _initConfig() {
		if (config.steppers==null) {
			config.steppers = new StepperConfig[2];
			config.steppers[0] = new StepperConfig(StepperFunction.values()[0]);
			config.steppers[0].serial = "71D1906F18002B00";
			config.steppers[1] = new StepperConfig(StepperFunction.values()[1]);
			config.steppers[1].serial = "???";
			try {
				cfgMgr.save(config);
//				System.out.println(cfgMgr.getText(config));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
 */

}
