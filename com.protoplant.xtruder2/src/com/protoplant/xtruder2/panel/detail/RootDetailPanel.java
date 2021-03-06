package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.PanelFocusEvent;
import com.protoplant.xtruder2.panel.summary.ConfigSummaryPanel;
import com.protoplant.xtruder2.panel.summary.ConveyanceSummaryPanel;
import com.protoplant.xtruder2.panel.summary.DataSummaryPanel;
import com.protoplant.xtruder2.panel.summary.SpoolingSummaryPanel;
import com.protoplant.xtruder2.panel.summary.StatusSummaryPanel;

public class RootDetailPanel extends Composite {

	private Logger log;
	protected StackLayout stack;
	protected Injector injector;
	
	protected ConveyanceDetailPanel pnlConv;
	protected StatusDetailPanel pnlStatus;
	protected ConfigDetailPanel pnlConfig;
	protected DataDetailPanel pnlData;
	protected SpoolingDetailPanel pnlSpooling;
	

	public RootDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setTouchEnabled(true);
		this.injector = injector;
		stack = new StackLayout();
		setLayout(stack);

		pnlStatus = new StatusDetailPanel(this, injector);
		pnlConv = new ConveyanceDetailPanel(this, injector);
		pnlSpooling = new SpoolingDetailPanel(this, injector);
		pnlData = new DataDetailPanel(this, injector);
		
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
			stack.topControl = pnlData;
			layout();
		} else if (w instanceof StatusSummaryPanel) {
			stack.topControl = pnlStatus;
			layout();
		} else if (w instanceof ConveyanceSummaryPanel) {
			stack.topControl = pnlConv;
			layout();
		} else if (w instanceof ConfigSummaryPanel) {
			stack.topControl = pnlConfig;
			layout(true, true);
			pnlConfig.onPanelFocus();
		}
	}
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
