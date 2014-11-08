package com.protoplant.xtruder2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

public class ControlPanel extends Composite {
	private IndicatorPanel pnlIndicator;
	private AnalogPanel pnlAnalog;
	private StepperPanel pnlStepper;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ControlPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		
		pnlIndicator = new IndicatorPanel(this, injector);
		pnlIndicator.setBounds(10, 10, 224, 200);
		
		pnlAnalog = new AnalogPanel(this, injector);
		pnlAnalog.setBounds(265, 10, 259, 200);
		
		XtruderConfig config = injector.getInstance(XtruderConfig.class);
		
		pnlStepper = new StepperPanel(this, injector, config.motors[0]);
		pnlStepper.setBounds(10, 216, 1189, 104);

		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
