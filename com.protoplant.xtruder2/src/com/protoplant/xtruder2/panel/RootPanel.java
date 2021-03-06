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

public class RootPanel extends SashForm {

	ProgressPanel pnlProgress;
	MainPanel pnlMain;
	private Logger log;
	
	public RootPanel(Composite parent, Injector injector) {
		super(parent, SWT.VERTICAL);
		setTouchEnabled(true);

		pnlProgress = new ProgressPanel(this, injector);
		pnlMain = new MainPanel(this, injector);
		
		setWeights(new int[] { 1, 9});
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
