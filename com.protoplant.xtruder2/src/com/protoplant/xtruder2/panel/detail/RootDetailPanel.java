package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.PanelFocusEvent;
import com.protoplant.xtruder2.panel.summary.ConveyanceSummaryPanel;
import com.protoplant.xtruder2.panel.summary.StatusSummaryPanel;

public class RootDetailPanel extends Composite {

	private Logger log;
	protected StackLayout stack;
	protected Injector injector;
	
	
	protected ConveyanceDetailPanel pnlConv;
	protected StatusDetailPanel pnlStatus;


	
	public RootDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		this.injector = injector;
		stack = new StackLayout();
		setLayout(stack);
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
	}

	@Subscribe
	public void onPanelFocus(PanelFocusEvent event) {
		Widget w = event.getWidget();
		if (w==null) return;
		log.info(w.toString());
		if (w instanceof StatusSummaryPanel) {
			if (pnlStatus == null) pnlStatus = new StatusDetailPanel(this, injector);
			stack.topControl = pnlStatus;
			layout();
		} else if (w instanceof ConveyanceSummaryPanel) {
			if (pnlConv == null) pnlConv = new ConveyanceDetailPanel(this, injector);
			stack.topControl = pnlConv;
			layout();
		}
	}
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
