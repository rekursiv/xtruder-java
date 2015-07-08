package com.protoplant.xtruder2.event;

public class SpoolTargetMassEvent {

	int mass;
	
	public SpoolTargetMassEvent(int mass) {
		this.mass = mass;
	}
	
	public int getMass() {
		return mass;
	}

}
