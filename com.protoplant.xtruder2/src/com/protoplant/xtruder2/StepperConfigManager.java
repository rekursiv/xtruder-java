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
		debugStepperMaps();
	}
	
	public void buildStepperMaps() {
		stepperSerialMap = new HashMap<String, Integer>();
		stepperFunctionMap = new HashMap<StepperFunction, Integer>();	
		for (int i=0; i<config.steppers.length; ++i) {
			if (config.steppers[i].serial!=null) stepperSerialMap.put(config.steppers[i].serial, i);
			stepperFunctionMap.put(config.steppers[i].function, i);
		}
	}
	
	public void debugStepperMaps() {
		System.out.println(stepperSerialMap.toString());
		System.out.println(stepperFunctionMap.toString());
	}
	
	public int getNumMappedSteppers() {
		return stepperSerialMap.size();
	}
	
	public StepperConfig getConfig(String serial) {
		Integer index = stepperSerialMap.get(serial);
		if (index==null) return null;
		else return config.steppers[index];
	}
	
	public StepperConfig getConfig(StepperFunction function) {
		Integer index = stepperFunctionMap.get(function);
		if (index==null) return null;
		else return config.steppers[index];
	}

	public StepperFunction getFunction(HIDDeviceInfo devInfo) {
		StepperConfig sc = getConfig(devInfo.getSerial_number());
		if (sc==null) {
			return StepperFunction.UNDEFINED;
		} else {
			return sc.function;
		}

	}
	

}
