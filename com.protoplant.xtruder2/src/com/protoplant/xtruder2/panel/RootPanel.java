package com.protoplant.xtruder2.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;
import com.protoplant.xtruder2.panel.detail.RootDetailPanel;
import com.protoplant.xtruder2.panel.summary.RootSummaryPanel;

import org.eclipse.swt.widgets.Group;

public class RootPanel extends SashForm {

	RootSummaryPanel rsp;
	RootDetailPanel rdp;
	
	public RootPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);

		rsp = new RootSummaryPanel(this, injector);
		rdp = new RootDetailPanel(this, injector);
		
		rsp.setDefaultFocus();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
