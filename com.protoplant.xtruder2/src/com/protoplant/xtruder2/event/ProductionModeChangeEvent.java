package com.protoplant.xtruder2.event;

import com.protoplant.xtruder2.panel.summary.ProductionPanel.Mode;

public class ProductionModeChangeEvent {

	private Mode mode;

	public ProductionModeChangeEvent(Mode mode) {
		this.mode = mode;
	}
	
	public Mode getMode() {
		return mode;
	}
	
}
