package com.protoplant.xtruder2.config;

public class AlarmConfig {
	
	public String mbrolaBase = "c:/mbrola";   //  /usr/share/mbrola
	public String voiceName = "mbrola_us1";   //  kevin16
	
	public int hopperFullThreshold = 1800;
	public int hopperDisconnectThreshold = 4000;
	public int hopperRepeatSeconds = 20;
	public int hopperAlarmSilenceSeconds = 240;	
	
	public float diaUpperThreshold = 1.77f;
	public float diaLowerThreshold = 1.71f;
	public int diaAlarmRepeatSeconds = 10;
	public int diaAlarmSilenceSeconds = 240;
	
	
}
