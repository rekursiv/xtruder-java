package com.protoplant.xtruder2.event;

public class AnalogDataEvent {
	
	private int pressure=0;
	private int mainHopper=0;
	private int auxHopper=0;
	private int aux=0;
	
	
	public AnalogDataEvent(int pressure, int polyHopper, int addHopper, int aux) {
		this.pressure=pressure;
		this.mainHopper=polyHopper;
		this.auxHopper=addHopper;
		this.aux=aux;
	}
	
	public int getPressure() {
		return pressure;
	}
	
	public int getMainHopper() {
		return mainHopper;
	}
	

}
