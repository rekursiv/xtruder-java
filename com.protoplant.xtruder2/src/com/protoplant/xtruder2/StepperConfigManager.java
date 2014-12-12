package com.protoplant.xtruder2;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import util.config.ConfigManager;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;

//protected final ConfigManager<StepperConfigList> cfgMgr = new ConfigManager<StepperConfigList>(StepperConfigList.class, "config/steppers.js");



@Singleton
public class StepperConfigManager {

	private Logger log;
	private XtruderConfig config;
	private ConfigManager<XtruderConfig> cfgMgr;
	private Map<String, StepperConfig> stepperSerialMap = new HashMap<String, StepperConfig>();
	private Map<StepperFunction, StepperConfig> stepperFunctionMap = new HashMap<StepperFunction, StepperConfig>();	

	@Inject
	public StepperConfigManager(Logger log, XtruderConfig config, ConfigManager<XtruderConfig> cfgMgr) {
		this.log = log;
		this.config = config;
		this.cfgMgr = cfgMgr;
		initConfig();
		buildStepperMaps();
	}

	private void buildStepperMaps() {
		for (StepperConfig sc : config.steppers) {
			stepperSerialMap.put(sc.serial, sc);
			stepperFunctionMap.put(sc.function, sc);
		}
	}

	private void initConfig() {
		if (config.steppers==null) {
			config.steppers = new StepperConfig[1];
			config.steppers[0] = new StepperConfig(StepperFunction.values()[0]);
			config.steppers[0].serial = "71D1906F18002B00";
			try {
//				cfgMgr.save(config);
				System.out.println(cfgMgr.getText(config));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public StepperConfig getConfig(String serial) {
		return stepperSerialMap.get(serial);
	}
	
	public StepperConfig getConfig(StepperFunction function) {
		return stepperFunctionMap.get(function);
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
	
	

}
