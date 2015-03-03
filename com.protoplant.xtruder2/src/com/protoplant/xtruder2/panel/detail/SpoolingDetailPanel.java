package com.protoplant.xtruder2.panel.detail;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.panel.AdjustableStepperPanel;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;


public class SpoolingDetailPanel extends Composite {

	protected AdjustableStepperPanel pnlWinderStepper;
	protected AdjustableStepperPanel pnlWinderMinderStepper;
	protected CoilMassPanel pnlCoilMass;

	public SpoolingDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FormLayout());
		
		pnlWinderStepper = new AdjustableStepperPanel(this, injector, StepperFunction.Winder);
		FormData fd_pnlWinderStepper = new FormData();
		fd_pnlWinderStepper.bottom = new FormAttachment(0, 137);
		fd_pnlWinderStepper.right = new FormAttachment(100, -12);
		fd_pnlWinderStepper.top = new FormAttachment(0, 10);
		fd_pnlWinderStepper.left = new FormAttachment(0, 12);
		pnlWinderStepper.setLayoutData(fd_pnlWinderStepper);
		pnlWinderStepper.setLayout(new FormLayout());
		
		pnlWinderMinderStepper = new AdjustableStepperPanel(this, injector, StepperFunction.WinderMinder);
		FormData fd_pnlWinderMinderStepper = new FormData();
		fd_pnlWinderMinderStepper.right = new FormAttachment(100, -12);
		fd_pnlWinderMinderStepper.left = new FormAttachment(pnlWinderStepper, 0, SWT.LEFT);
		fd_pnlWinderMinderStepper.top = new FormAttachment(0, 145);
		pnlWinderMinderStepper.setLayoutData(fd_pnlWinderMinderStepper);
		
		Composite pnlCoilMass = new CoilMassPanel(this, injector);
		fd_pnlWinderMinderStepper.bottom = new FormAttachment(pnlCoilMass, -6);
		FormData fd_pnlCoilMass = new FormData();
		fd_pnlCoilMass.bottom = new FormAttachment(100, -14);
		fd_pnlCoilMass.right = new FormAttachment(100, -12);
		fd_pnlCoilMass.top = new FormAttachment(0, 335);
		fd_pnlCoilMass.left = new FormAttachment(0, 17);
		pnlCoilMass.setLayoutData(fd_pnlCoilMass);
		
//		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
