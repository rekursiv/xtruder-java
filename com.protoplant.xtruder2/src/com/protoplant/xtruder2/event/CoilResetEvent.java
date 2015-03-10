package com.protoplant.xtruder2.event;

public class CoilResetEvent {

	private Object isWrapAround;

	public CoilResetEvent(boolean isWrapAround) {
		this.setWrapAround(isWrapAround);
	}

	public Object isWrapAround() {
		return isWrapAround;
	}

	public void setWrapAround(Object isWrapAround) {
		this.isWrapAround = isWrapAround;
	}

	
	
}
