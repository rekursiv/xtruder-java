package com.protoplant.xtruder2.event;

public class AnalogDataEvent {
	
	private int pressure=0;
	private int polyHopper=0;
	private int addHopper=0;
	private int aux=0;
	
	
	public AnalogDataEvent(int pressure, int polyHopper, int addHopper, int aux) {
		this.pressure=pressure;
		this.polyHopper=polyHopper;
		this.addHopper=addHopper;
		this.aux=aux;
	}
	
	public int getPressure() {
		return pressure;
	}
	
	
}
