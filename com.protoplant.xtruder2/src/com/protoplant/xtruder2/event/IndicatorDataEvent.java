package com.protoplant.xtruder2.event;

public class IndicatorDataEvent {
	
	private float cur;
	private float min;
	private float max;
	
	
	public IndicatorDataEvent(float cur, float min, float max) {
		this.min = min;
		this.cur = cur;
		this.max = max;
	}
	
	public float getCur() {
		return cur;
	}
	public float getMin() {
		return min;
	}
	public float getMax() {
		return max;
	}


}
