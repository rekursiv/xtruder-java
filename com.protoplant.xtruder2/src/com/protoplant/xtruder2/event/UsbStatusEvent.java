package com.protoplant.xtruder2.event;

public class UsbStatusEvent {

	private int numDevs = 0;
	
	public UsbStatusEvent(int numDevs) {
		this.numDevs=numDevs;
		System.out.println("==="+numDevs);
	}

	public int getNumDevs() {
		return numDevs;
	}
}
