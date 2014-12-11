package com.protoplant.xtruder2.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperType;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;

import org.eclipse.swt.widgets.Slider;

public class AdjustableStepperPanel extends StepperPanel {
	protected Slider sldSpeed;
	protected Button btnStart;


	public AdjustableStepperPanel(Composite parent, Injector injector, StepperType type) {
		super(parent, null, type);
		
		sldSpeed = new Slider(this, SWT.NONE);
		sldSpeed.setBounds(10, 52, 536, 34);
		sldSpeed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				onAdjust();
			}
		});
		sldSpeed.setPageIncrement(20);
		sldSpeed.setMaximum(32000);
		sldSpeed.setSelection(5000);
		
		btnStart = new Button(this, SWT.NONE);
		btnStart.setBounds(10, 95, 75, 25);
		btnStart.setText("Start");

		if (injector!=null) injector.injectMembers(this);
	}
	

	public void onAdjust() {
		int sliderVal = sldSpeed.getSelection();
		eb.post(new StepperSpeedChangeEvent(type, sliderVal));
	}

	
	@Override
	public void onSpeedChange(StepperSpeedChangeEvent evt) {
		super.onSpeedChange(evt);
//		log.info("");
	}


	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
