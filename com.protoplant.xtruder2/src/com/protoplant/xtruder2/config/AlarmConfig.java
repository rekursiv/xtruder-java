package com.protoplant.xtruder2.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmConfig {
	
	public String mbrolaBase = "c:/mbrola";   //  /usr/share/mbrola
	public String voiceName = "mbrola_us1";   //  kevin16
	
	public int hopperEmptyThreshold = 2500;
	public int hopperDisconnectThreshold = 400;
	
	public int hopperRepeatSeconds = 20;
	public int hopperAlarmSilenceSeconds = 240;	
	
	public float diaUpperThreshold = 1.77f;
	public float diaLowerThreshold = 1.71f;
	public int diaAlarmRepeatSeconds = 10;
	public int diaAlarmResetSeconds = 1;
	public int diaAlarmSilenceSeconds = 240;
	public int diaOverCountTrigger = 1;
	public int diaUnderCountTrigger =1;

	public int pressureMax = 2000;
	public int pressureAlarmSilenceSeconds = 240;
	public int pressureAlarmRepeastSeconds = 10;
	
	
}
