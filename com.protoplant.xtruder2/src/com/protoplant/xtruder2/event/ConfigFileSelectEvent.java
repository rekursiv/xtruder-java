package com.protoplant.xtruder2.event;

public class ConfigFileSelectEvent {

	private String name;
	
	public ConfigFileSelectEvent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
