package com.protoplant.xtruder2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

public class ControlPanel extends Composite {
	private IndicatorPanel pnlIndicator;
	private AnalogPanel pnlAnalog;
	private StepperPanel pnlStepper1;
	private StepperPanel pnlStepper2;
	private StepperPanel stepperPanel;
	private StepperPanel stepperPanel_1;
	private StepperPanel stepperPanel_2;
	private StepperPanel stepperPanel_3;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ControlPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		
		pnlIndicator = new IndicatorPanel(this, injector);
		pnlIndicator.setBounds(902, 340, 405, 104);
		
		pnlAnalog = new AnalogPanel(this, injector);
		pnlAnalog.setBounds(902, 120, 328, 104);
		
		pnlStepper1 = new StepperPanel(this, injector, 0);
		pnlStepper1.setBounds(10, 10, 858, 104);

		pnlStepper2 = new StepperPanel(this, injector, 1);
		pnlStepper2.setBounds(10, 120, 858, 104);
		
		stepperPanel = new StepperPanel(this, injector, 2);
		stepperPanel.setBounds(10, 230, 858, 104);
		
		stepperPanel_1 = new StepperPanel(this, injector, 3);
		stepperPanel_1.setBounds(10, 340, 858, 104);
		
		stepperPanel_2 = new StepperPanel(this, injector, 4);
		stepperPanel_2.setBounds(10, 450, 858, 104);
		
		stepperPanel_3 = new StepperPanel(this, injector, 5);
		stepperPanel_3.setBounds(10, 560, 858, 104);
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
