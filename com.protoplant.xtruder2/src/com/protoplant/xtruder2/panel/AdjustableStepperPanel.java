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
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;
import com.protoplant.xtruder2.event.StepperStopEvent;

import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class AdjustableStepperPanel extends StepperPanel {
	
	protected Scale sldSpeed;	
	protected Button btnRunStop;

	public AdjustableStepperPanel(Composite parent, Injector injector, StepperFunction type) {
		super(parent, null, type);

		sldSpeed = new Scale(this, SWT.NONE);
		sldSpeed.setTouchEnabled(true);
		FormData fd_sldSpeed = new FormData();
		fd_sldSpeed.bottom = new FormAttachment(0, 74);
		fd_sldSpeed.right = new FormAttachment(100, -12);
		fd_sldSpeed.top = new FormAttachment(0, 28);
		fd_sldSpeed.left = new FormAttachment(0, 7);
		sldSpeed.setLayoutData(fd_sldSpeed);
		sldSpeed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				adjustSpeed();
			}
		});
		sldSpeed.setPageIncrement(20);
		sldSpeed.setMaximum(32000);
//		sldSpeed.setSelection(5000);
		
		btnRunStop = new Button(this, SWT.NONE);
		FormData fd_btnRunStop = new FormData();
		fd_btnRunStop.right = new FormAttachment(0, 82);
		fd_btnRunStop.top = new FormAttachment(0, 80);
		fd_btnRunStop.left = new FormAttachment(0, 7);
		btnRunStop.setLayoutData(fd_btnRunStop);
		btnRunStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (isRunning) stop();
				else run();
			}
		});
		btnRunStop.setText("Run");

		if (injector!=null) injector.injectMembers(this);
	}
	
//	@Override
//	public void onSpeedChange(StepperSpeedChangeEvent evt) {
//		super.onSpeedChange(evt);
//		log.info("");
//	}
	
	
	
	@Override
	public void onConfigSetup(ConfigSetupEvent evt) {
		super.onConfigSetup(evt);
		sldSpeed.setMinimum(scm.getConfig(function).speedSliderMin);
		sldSpeed.setSelection(scm.getConfig(function).speedSliderInit);
		sldSpeed.setMaximum(scm.getConfig(function).speedSliderMax);
		adjustSpeed();
//		log.info("A:"+function.name()+"   "+scm.getConfig(function).speedSliderInit);
	}
	
	@Subscribe
	public void onConfigStore(ConfigStoreEvent evt) {
		scm.getConfig(function).speedSliderInit = sldSpeed.getSelection();
	}
	
	@Override
	protected void adjustSpeed() {
		speed = sldSpeed.getSelection();
		super.adjustSpeed();
	}
	
	@Override
	public void run() {
		btnRunStop.setText("Stop");
		super.run();
	}
	
	@Override
	public void stop() {
		btnRunStop.setText("Run");
		super.stop();
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
