package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.XtruderConfig;
import com.protoplant.xtruder2.event.StepperConfigChangeEvent;
import com.protoplant.xtruder2.panel.AdjustableStepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

public class TestDetailPanel extends Composite {

	private Logger log;
	private EventBus eb;
	private XtruderConfig config;
	
	protected Button btnTest;
	
	protected AdjustableStepperPanel asp;
	protected TrackingStepperPanel tsp;
	protected Composite composite;
	protected Label lblTest;


	public TestDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		
		asp = new AdjustableStepperPanel(this, injector, StepperFunction.TopRoller);
		asp.setBounds(10, 10, 592, 156);
		
		tsp = new TrackingStepperPanel(this, injector, StepperFunction.BottomRoller, StepperFunction.TopRoller);
		tsp.setBounds(10, 191, 592, 156);

		
		btnTest = new Button(this, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
//				config.test++;
//				eb.post(new StepperConfigChangeEvent(StepperFunction.TopRoller));
				lblTest.setText(""+config.test);
			}
		});
		btnTest.setBounds(10, 403, 75, 25);
		btnTest.setText("TEST");
		
		lblTest = new Label(this, SWT.NONE);
		lblTest.setBounds(134, 413, 55, 15);
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config) {
		this.log = log;
		this.eb = eb;
		this.config = config;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
