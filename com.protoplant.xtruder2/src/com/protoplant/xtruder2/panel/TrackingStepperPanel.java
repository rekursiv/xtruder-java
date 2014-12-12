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
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;

import org.eclipse.swt.widgets.Slider;

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
		
		this.typeToTrack = typeToTrack;
		
		chkTracking = new Button(this, SWT.CHECK);
		chkTracking.setSelection(true);
		chkTracking.setBounds(193, 92, 162, 16);
		chkTracking.setText("Tracking: "+typeToTrack);
		
		lblSF_title = new Label(this, SWT.NONE);
		lblSF_title.setBounds(380, 92, 101, 15);
		lblSF_title.setText("Scale Factor:");
		
		lblScaleFactor = new Label(this, SWT.NONE);
		lblScaleFactor.setBounds(487, 93, 55, 15);
		lblScaleFactor.setText("1.0");
		
		if (injector!=null) injector.injectMembers(this);
	}
	
	
	@Override
	public void onAdjust() {
		calcScaleFactor();
		eb.post(new StepperSpeedChangeEvent(function, sldSpeed.getSelection()));
	}
	
	public void calcScaleFactor() {
		if (trackedSpeed>0) {
			speedScaleFactor = (float)sldSpeed.getSelection()/(float)trackedSpeed;
			lblScaleFactor.setText(String.format("%.4f", speedScaleFactor));
		}
	}
	
	@Override
	public void onSpeedChange(StepperSpeedChangeEvent evt) {
		super.onSpeedChange(evt);
		if (evt.getFunction()==typeToTrack&&typeToTrack!=function) {
			trackedSpeed = evt.getSpeed();
			if (chkTracking.getSelection()) {
	//			log.info(""+evt.getSpeed());
				int speed = (int)((float)trackedSpeed*speedScaleFactor);
				sldSpeed.setSelection(speed);
				super.onAdjust();
			} else {
				calcScaleFactor();
			}
		}
	}


	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
