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

	public SpoolingDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FormLayout());
		
		pnlWinderStepper = new AdjustableStepperPanel(this, injector, StepperFunction.Winder);
		FormData fd_pnlWinderStepper = new FormData();
		fd_pnlWinderStepper.bottom = new FormAttachment(0, 145);
		fd_pnlWinderStepper.right = new FormAttachment(100, -12);
		fd_pnlWinderStepper.top = new FormAttachment(0, 10);
		fd_pnlWinderStepper.left = new FormAttachment(0, 12);
		pnlWinderStepper.setLayoutData(fd_pnlWinderStepper);
		pnlWinderStepper.setLayout(new FormLayout());
		
		pnlWinderMinderStepper = new AdjustableStepperPanel(this, injector, StepperFunction.WinderMinder);
		FormData fd_pnlWinderMinderStepper = new FormData();
		fd_pnlWinderMinderStepper.right = new FormAttachment(pnlWinderStepper, 0, SWT.RIGHT);
		fd_pnlWinderMinderStepper.left = new FormAttachment(0, 12);
		fd_pnlWinderMinderStepper.bottom = new FormAttachment(0, 280);
		pnlWinderMinderStepper.setLayoutData(fd_pnlWinderMinderStepper);
		
//		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
