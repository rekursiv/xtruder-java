package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.PanelFocusEvent;
import com.protoplant.xtruder2.panel.summary.AlarmSummaryPanel;
import com.protoplant.xtruder2.panel.summary.ConfigSummaryPanel;
import com.protoplant.xtruder2.panel.summary.ConveyanceSummaryPanel;
import com.protoplant.xtruder2.panel.summary.DataSummaryPanel;
import com.protoplant.xtruder2.panel.summary.RootSummaryPanel;
import com.protoplant.xtruder2.panel.summary.SpoolingSummaryPanel;
import com.protoplant.xtruder2.panel.summary.StatusSummaryPanel;
import com.protoplant.xtruder2.panel.summary.TestSummaryPanel;

public class RootDetailPanel extends Composite {

	private Logger log;
	protected StackLayout stack;
	protected Injector injector;
	
	protected TestDetailPanel pnlTest;
	protected AlarmDetailPanel pnlAlarm;
	protected ConveyanceDetailPanel pnlConv;
	protected StatusDetailPanel pnlStatus;
	protected ConfigDetailPanel pnlConfig;
	protected DataDetailPanel pnlPressure;
	protected SpoolingDetailPanel pnlSpooling;
	

	
	public RootDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setTouchEnabled(true);
		this.injector = injector;
		stack = new StackLayout();
		setLayout(stack);
		

		pnlPressure = new DataDetailPanel(this, injector);
//		pnlTest = new TestDetailPanel(this, injector);
		pnlStatus = new StatusDetailPanel(this, injector);
		pnlConv = new ConveyanceDetailPanel(this, injector);
		pnlSpooling = new SpoolingDetailPanel(this, injector);
		pnlAlarm = new AlarmDetailPanel(this, injector);
		
		pnlConfig = new ConfigDetailPanel(this, injector);   //  create this LAST  (loads config on construct)
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log) {
		this.log = log;
	}

	@Subscribe
	public void onPanelFocus(PanelFocusEvent event) {
		Widget w = event.getWidget();
		if (w==null) return;
//		log.info(w.toString());

		if (w instanceof SpoolingSummaryPanel) {
			stack.topControl = pnlSpooling;
			layout();
		} else if (w instanceof DataSummaryPanel) {
			stack.topControl = pnlPressure;
			layout();
		} else if (w instanceof AlarmSummaryPanel) {
			stack.topControl = pnlAlarm;
			layout();
		} else if (w instanceof TestSummaryPanel) {
//			stack.topControl = pnlTest;
//			layout();
		} else if (w instanceof StatusSummaryPanel) {
			stack.topControl = pnlStatus;
			layout();
		} else if (w instanceof ConveyanceSummaryPanel) {
			stack.topControl = pnlConv;
			layout();
		} else if (w instanceof ConfigSummaryPanel) {
			stack.topControl = pnlConfig;
			layout(true, true);
			layout(true, true);  // gotta do this TWICE on Linux to force layout of text editors, no idea why...
			pnlConfig.onPanelFocus();
		}
	}
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
