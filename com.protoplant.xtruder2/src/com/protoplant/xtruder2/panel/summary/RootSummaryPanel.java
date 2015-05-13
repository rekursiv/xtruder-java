package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.Test;
import com.protoplant.xtruder2.event.PanelFocusEvent;
import com.protoplant.xtruder2.panel.detail.TestDetailPanel;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class RootSummaryPanel extends Composite {

	private Logger log;
	private EventBus eb;
	
	protected StatusSummaryPanel pnlStatus;
	protected ConveyanceSummaryPanel pnlConv;
	protected TestSummaryPanel pnlTest;
	protected ConfigSummaryPanel pnlConfig;
	protected DataSummaryPanel pnlData;
	protected SpoolingSummaryPanel pnlSpooling;
	protected AlarmSummaryPanel pnlAlarm;
	private Test test;

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
		pnlData.setBounds(10, 298, 366, 90);

		pnlSpooling = new SpoolingSummaryPanel(this, injector);
		pnlSpooling.setBounds(196, 106, 180, 90);
		
		pnlAlarm = new AlarmSummaryPanel(this, injector);
		pnlAlarm.setBounds(10, 202, 180, 90);
		
//		pnlTest = new TestSummaryPanel(this, injector);
//		pnlTest.setBounds(196, 202, 180, 90);
		
		Button btnTest = new Button(this, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				test.test();
			}
		});
		btnTest.setBounds(10, 429, 75, 25);
		btnTest.setText("TEST");
		
		if (injector!=null) injector.injectMembers(this);
	}
	
	@Inject
	public void inject(Logger log, EventBus eb, Test test) {
		this.log = log;
		this.eb = eb;
		this.test = test;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setDefaultFocus() {
		eb.post(new PanelFocusEvent(pnlStatus));  //pnlStatus
	}
}
