package com.protoplant.xtruder2.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConveyanceConfig {
	
	public int speedSliderMin = 1000;
	public int speedSliderInit = 4000;
	public int speedSliderMax = 16000;
	
}
