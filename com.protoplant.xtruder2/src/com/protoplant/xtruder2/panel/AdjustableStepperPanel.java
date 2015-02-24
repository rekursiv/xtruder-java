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
	private Label lblPosition;

	public AdjustableStepperPanel(Composite parent, Injector injector, StepperFunction type) {
		super(parent, null, type);

		sldSpeed = new Scale(this, SWT.NONE);
		sldSpeed.setTouchEnabled(true);
		FormData fd_sldSpeed = new FormData();
		fd_sldSpeed.top = new FormAttachment(lblSetpt);
		fd_sldSpeed.bottom = new FormAttachment(0, 74);
		sldSpeed.setLayoutData(fd_sldSpeed);
		sldSpeed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				adjustSpeed();
			}
		});
		sldSpeed.setPageIncrement(20);
//		sldSpeed.setMaximum(32000);
//		sldSpeed.setSelection(5000);
		
		btnRunStop = new Button(this, SWT.NONE);
		FormData fd_btnRunStop = new FormData();
		fd_btnRunStop.top = new FormAttachment(sldSpeed, 6);
		fd_btnRunStop.right = new FormAttachment(0, 82);
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
		
		Button btnLeft = new Button(this, SWT.NONE);
		btnLeft.setTouchEnabled(true);
		btnLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				sldSpeed.setSelection(sldSpeed.getSelection()-1);
//				isReversed=!isReversed;                                       //   TEST
				adjustSpeed();
			}
		});
		fd_sldSpeed.left = new FormAttachment(btnLeft);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.bottom = new FormAttachment(sldSpeed, 0, SWT.BOTTOM);
		fd_btnNewButton.top = new FormAttachment(lblSetpt);
		fd_btnNewButton.right = new FormAttachment(btnRunStop, 25);
		fd_btnNewButton.left = new FormAttachment(btnRunStop, 0, SWT.LEFT);
		btnLeft.setLayoutData(fd_btnNewButton);
		btnLeft.setText("<");
		
		Button btnRight = new Button(this, SWT.NONE);
		btnRight.setTouchEnabled(true);
		btnRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				sldSpeed.setSelection(sldSpeed.getSelection()+1);
				adjustSpeed();
			}
		});
		fd_sldSpeed.right = new FormAttachment(btnRight);
		btnRight.setText(">");
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(sldSpeed, -47);
		fd_button.bottom = new FormAttachment(sldSpeed, 0, SWT.BOTTOM);
		fd_button.right = new FormAttachment(100, -12);
		fd_button.left = new FormAttachment(100, -37);
		btnRight.setLayoutData(fd_button);
		
		lblPosition = new Label(this, SWT.NONE);
		FormData fd_lblPosition = new FormData();
		fd_lblPosition.bottom = new FormAttachment(0, 160);
		fd_lblPosition.right = new FormAttachment(0, 280);
		fd_lblPosition.top = new FormAttachment(0, 125);
		fd_lblPosition.left = new FormAttachment(0, 7);
		lblPosition.setLayoutData(fd_lblPosition);
		lblPosition.setText("Position:");

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
		sldSpeed.setMaximum(scm.getConfig(function).speedSliderMax);
		sldSpeed.setMinimum(scm.getConfig(function).speedSliderMin);
		sldSpeed.setSelection(scm.getConfig(function).speedSliderInit);
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
	public void updatePos(int position) {
		//  FIXME:  this is a hack to get position info to Adjustable subclass
		lblPosition.setText("Position: "+position);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
