package com.protoplant.xtruder2.panel.summary;

import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

public class ConveyanceSummaryPanel extends BaseSummaryPanel {

	public ConveyanceSummaryPanel(Composite parent, Injector injector) {
		super(parent, injector);
		setText("Extruder Conveyance");
		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
