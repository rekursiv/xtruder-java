package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.PanelFocusEvent;

public class RootSummaryPanel extends Composite {

	private Logger log;
	private EventBus eb;
	
	protected StatusSummaryPanel pnlStatus;
	protected ConveyanceSummaryPanel pnlConv;
	protected ConfigSummaryPanel pnlConfig;
	protected DataSummaryPanel pnlData;
	protected SpoolingSummaryPanel pnlSpooling;
	
	protected ProductionPanel pnlProduction;


	public RootSummaryPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setTouchEnabled(true);
		
		pnlStatus = new StatusSummaryPanel(this, injector);
		pnlStatus.setBounds(10, 10, 180, 90);
		
		pnlConv = new ConveyanceSummaryPanel(this, injector);
		pnlConv.setBounds(10, 106, 180, 90);
		
		pnlConfig = new ConfigSummaryPanel(this, injector);
		pnlConfig.setBounds(196, 10, 180, 90);
		
		pnlData = new DataSummaryPanel(this, injector);
		pnlData.setBounds(10, 202, 366, 90);

		pnlSpooling = new SpoolingSummaryPanel(this, injector);
		pnlSpooling.setBounds(196, 106, 180, 90);
		
		pnlProduction = new ProductionPanel(this, injector);
		pnlProduction.setBounds(10, 298, 366, 536);
		
		setTabList(new Control[]{});
		
		if (injector!=null) injector.injectMembers(this);
	}
	
	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
		setDefaultFocus();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setDefaultFocus() {
		eb.post(new PanelFocusEvent(pnlConv));  //pnlStatus
	}
}
