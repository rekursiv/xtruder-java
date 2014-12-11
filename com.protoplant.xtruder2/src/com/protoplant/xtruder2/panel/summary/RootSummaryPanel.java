package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.PanelFocusEvent;

public class RootSummaryPanel extends Composite {

	protected StatusSummaryPanel pnlStatus;
	protected ConveyanceSummaryPanel pnlConv;
	private Logger log;
	private EventBus eb;

	public RootSummaryPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		
		pnlStatus = new StatusSummaryPanel(this, injector);
		pnlStatus.setBounds(10, 10, 171, 82);
		
		pnlConv = new ConveyanceSummaryPanel(this, injector);
		pnlConv.setBounds(10, 98, 171, 94);
		
		if (injector!=null) injector.injectMembers(this);
	}
	
	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
		

	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setDefaultFocus() {
		eb.post(new PanelFocusEvent(this));
	}

}
