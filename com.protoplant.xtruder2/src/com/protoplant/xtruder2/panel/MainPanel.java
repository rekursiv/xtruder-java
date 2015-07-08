package com.protoplant.xtruder2.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.panel.detail.RootDetailPanel;
import com.protoplant.xtruder2.panel.summary.RootSummaryPanel;

import org.eclipse.swt.widgets.Group;

public class MainPanel extends SashForm {

	RootSummaryPanel rsp;
	RootDetailPanel rdp;
	private Logger log;
	
	public MainPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		setTouchEnabled(true);

		rsp = new RootSummaryPanel(this, injector);
		rdp = new RootDetailPanel(this, injector);
		
		setWeights(new int[] { 3, 9});
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				rsp.setDefaultFocus();
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
