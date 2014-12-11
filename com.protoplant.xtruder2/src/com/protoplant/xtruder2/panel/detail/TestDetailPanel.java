package com.protoplant.xtruder2.panel.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperType;
import com.protoplant.xtruder2.panel.AdjustableStepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;

public class TestDetailPanel extends Composite {
	protected AdjustableStepperPanel asp;

	protected TrackingStepperPanel tsp;
	protected Composite composite;

	public TestDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		
		asp = new AdjustableStepperPanel(this, injector, StepperType.TopRoller);
		asp.setBounds(10, 10, 497, 156);
		
//		tsp = new TrackingStepperPanel(this, injector, StepperType.TopRoller, StepperType.BottomRoller);
//		tsp.setBounds(10, 191, 497, 156);

//		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
