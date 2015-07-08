package com.protoplant.xtruder2.event;

public class CoilResetEvent {

	public enum Context {SILENT, RESET, WRAP};
	
	private Context context;

	public CoilResetEvent(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	
	
}
