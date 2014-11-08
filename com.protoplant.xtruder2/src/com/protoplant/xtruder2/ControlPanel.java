package com.protoplant.xtruder2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

public class ControlPanel extends Composite {
	private IndicatorPanel pnlIndicator;
	private AnalogPanel pnlAnalog;
	private StepperPanel pnlStepper1;
	private StepperPanel pnlStepper2;
	
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
		
		pnlStepper1 = new StepperPanel(this, injector, 0);
		pnlStepper1.setBounds(10, 220, 858, 104);

		pnlStepper2 = new StepperPanel(this, injector, 1);
		pnlStepper2.setBounds(10, 330, 858, 104);
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
