package com.protoplant.xtruder2.config;

import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.config.ConfigManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.protoplant.xtruder2.StepperFunction;

@Singleton
public class StepperConfigManager {

	private Logger log;
	private XtruderConfig config;
	private ConfigManager<XtruderConfig> cfgMgr;

	private MachineState machineState = new MachineState();


	@Inject
	public StepperConfigManager(Logger log, XtruderConfig config, ConfigManager<XtruderConfig> cfgMgr) {
		this.log = log;
		this.config = config;
		this.cfgMgr = cfgMgr;
		initConfig();
	}

	private void initConfig() {
		if (config.steppers==null) {
			log.info("");
			config.steppers = new TreeMap<StepperFunction, StepperConfig>();

			int numMotors = StepperFunction.values().length-1;  // last enum = "UNDEFINED" 
			for (int i=0; i<numMotors; ++i) {
				StepperFunction function = StepperFunction.values()[i];
				config.steppers.put(function, new StepperConfig());
			}
			
			try {
				cfgMgr.save(config);
			} catch (Exception e) {
				log.log(Level.WARNING, "Error saving steppers to config file: ", e);
			}
		}
	}
	

	public int getNumDefinedSteppers() {
		int num = 0;
		for (StepperConfig sc : config.steppers.values()) {
			if (sc.serial!=null) ++num;
		}
		return num;
	}

	public StepperConfig getConfig(StepperFunction function) {
		return config.steppers.get(function);
	}

	public StepperFunction getFunction(String serial) {
		StepperFunction function = StepperFunction.UNDEFINED;
		for (Entry<StepperFunction, StepperConfig> entry : config.steppers.entrySet()) {
			if (entry.getValue().serial.equals(serial)) return entry.getKey();
		}
		return function;
	}

	public void storeConveyanceState(int speedSliderInit) {
		machineState.conveyance.speedSliderInit = speedSliderInit;
	}
	
	public void storeStepperState(StepperFunction function, int speedSliderInit) {
		machineState.steppers.put(function, new StepperState(speedSliderInit));
	}

	public void storeStepperState(StepperFunction function, int speedSliderInit, boolean isTracking, float trackingScaleFactor) {
		machineState.steppers.put(function, new StepperState(speedSliderInit, isTracking, trackingScaleFactor));
	}

	public MachineState getMachineState() {
		return machineState;
	}
	
	
	public void test() {
		try {
			System.out.println(cfgMgr.getText(machineState));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
