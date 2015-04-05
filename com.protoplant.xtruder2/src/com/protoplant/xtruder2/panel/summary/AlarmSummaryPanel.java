package com.protoplant.xtruder2.panel.summary;

import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

public class AlarmSummaryPanel extends BaseSummaryPanel {

	public AlarmSummaryPanel(Composite parent, Injector injector) {
		super(parent, injector);
		setText("Alarm");
		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
