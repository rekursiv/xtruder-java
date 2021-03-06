package com.protoplant.xtruder2.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackConfig {

	public float targetDiameter = 1.73f;
	public float deadband = 0.015f;
	public float spread = 0.1f;
	public float nudgeFactor = 50.0f;
	public int period = 10;  // num of clocks (USB packets) between feedback calculations

}
