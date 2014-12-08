package com.protoplant.xtruder2.panel.summary;

import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

public class StatusSummaryPanel extends BaseSummaryPanel {

	public StatusSummaryPanel(Composite parent, Injector injector) {
		super(parent, injector);
		setText("System Status");
		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
