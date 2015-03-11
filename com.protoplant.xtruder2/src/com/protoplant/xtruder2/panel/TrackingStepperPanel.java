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
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class TrackingStepperPanel extends AdjustableStepperPanel {
	
	protected StepperFunction typeToTrack;
	protected int trackedSpeed = 0;         //  FIXME: do proper init
	protected float speedScaleFactor = 1.0f;
	
	protected Slider slider;
	protected Button chkTracking;
	protected Label lblSF_title;
	protected Label lblScaleFactor;
	

	public TrackingStepperPanel(Composite parent, Injector injector, StepperFunction type, StepperFunction typeToTrack) {
		super(parent, null, type);
		btnReset.setTouchEnabled(true);
		btnRunStop.setTouchEnabled(true);
		sldSpeed.setTouchEnabled(true);
		
		this.typeToTrack = typeToTrack;
		
		chkTracking = new Button(this, SWT.CHECK);
		chkTracking.setTouchEnabled(true);
		FormData fd_chkTracking = new FormData();
		fd_chkTracking.bottom = new FormAttachment(btnRunStop, 25);
		fd_chkTracking.top = new FormAttachment(btnRunStop, 0, SWT.TOP);
		fd_chkTracking.right = new FormAttachment(0, 375);
		fd_chkTracking.left = new FormAttachment(0, 100);
		chkTracking.setLayoutData(fd_chkTracking);
		chkTracking.setBounds(193, 92, 162, 16);
		chkTracking.setText("Tracking: "+typeToTrack);
		
		lblSF_title = new Label(this, SWT.NONE);
		FormData fd_lblSF_title = new FormData();
		fd_lblSF_title.bottom = new FormAttachment(chkTracking, 0, SWT.BOTTOM);
		fd_lblSF_title.top = new FormAttachment(chkTracking, 0, SWT.TOP);
		lblSF_title.setLayoutData(fd_lblSF_title);
		lblSF_title.setBounds(380, 92, 101, 15);
		lblSF_title.setText("Scale Factor:");
		
		lblScaleFactor = new Label(this, SWT.NONE);
		fd_lblSF_title.left = new FormAttachment(0, 416);
		fd_lblSF_title.right = new FormAttachment(0, 520);
		FormData fd_lblScaleFactor = new FormData();
		fd_lblScaleFactor.bottom = new FormAttachment(lblSF_title, 0, SWT.BOTTOM);
		fd_lblScaleFactor.left = new FormAttachment(btnReset, 0, SWT.LEFT);
		fd_lblScaleFactor.right = new FormAttachment(btnReset, 0, SWT.RIGHT);
		fd_lblScaleFactor.top = new FormAttachment(sldSpeed, 6);
		lblScaleFactor.setLayoutData(fd_lblScaleFactor);
		lblScaleFactor.setBounds(487, 93, 55, 15);
		lblScaleFactor.setText("1.0");
		
		if (injector!=null) injector.injectMembers(this);
	}
	
	
	@Override
	public void onConfigSetup(ConfigSetupEvent evt) {
		super.onConfigSetup(evt);
		chkTracking.setSelection(scm.getConfig(function).isTracking);
		speedScaleFactor = scm.getConfig(function).trackingScaleFactor;
		lblScaleFactor.setText(String.format("%.4f", speedScaleFactor));
//		log.info("T:"+function.name()+scm.getConfig(function).speedSetPoint);
	}
	
	@Override
	public void onConfigStore(ConfigStoreEvent evt) {
		scm.storeStepperState(function, sldSpeed.getSelection(), chkTracking.getSelection(), speedScaleFactor);
	}
	
	@Override
	public void adjustSpeed() {
		super.adjustSpeed();
		calcScaleFactor();
	}
	
	public void calcScaleFactor() {
		if (trackedSpeed>0) {
			speedScaleFactor = (float)sldSpeed.getSelection()/(float)trackedSpeed;
			lblScaleFactor.setText(String.format("%.4f", speedScaleFactor));
		}
	}
	
	@Subscribe
	public void onSpeedChange(StepperSpeedChangeEvent evt) {
//		super.onSpeedChange(evt);
		if (evt.getFunction()==typeToTrack&&typeToTrack!=function) {
			trackedSpeed = Math.abs(evt.getSpeed());
			if (chkTracking.getSelection()) {
//				log.info(""+evt.getSpeed());
				int speed = (int)((float)trackedSpeed*speedScaleFactor);
				sldSpeed.setSelection(speed);
				super.adjustSpeed();
			} else {
				calcScaleFactor();
			}
		}
	}
	
	@Subscribe
	public void onRun(StepperRunEvent evt) {
		if (evt.getFunction()==typeToTrack&&typeToTrack!=function) {
			if (!isRunning) run();
		}
	}

	@Subscribe
	public void onStop(StepperStopEvent evt) {
		if (evt.getFunction()==typeToTrack&&typeToTrack!=function) {
			if (isRunning) stop();
		}
	}
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
