package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStopEvent;
import com.protoplant.xtruder2.panel.StepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class ConveyanceDetailPanel extends Composite {

	private Logger log;
	private EventBus eb;
	protected Group grpPinchRoller;
	protected Slider slider;
	protected StepperPanel stepperPinch1;
	protected StepperPanel stepperPanel;
	protected Button btnRunStop;
	protected Group grpTakeupWheels;
	protected TrackingStepperPanel pnlTopRoller;
	protected TrackingStepperPanel pnlBtmRoller;
	private boolean isRunning = false;

	public ConveyanceDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FormLayout());
		
		grpPinchRoller = new Group(this, SWT.NONE);
		FormData fd_grpPinchRoller = new FormData();
		fd_grpPinchRoller.bottom = new FormAttachment(0, 335);
		fd_grpPinchRoller.right = new FormAttachment(100, -10);
		fd_grpPinchRoller.top = new FormAttachment(0, 10);
		fd_grpPinchRoller.left = new FormAttachment(0, 10);
		grpPinchRoller.setLayoutData(fd_grpPinchRoller);
		grpPinchRoller.setText("Pinch Rollers");
		grpPinchRoller.setLayout(new FormLayout());
		
		slider = new Slider(grpPinchRoller, SWT.NONE);
		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				adjustSpeed();
			}
		});
		slider.setPageIncrement(20);
		slider.setMaximum(32000);
		slider.setSelection(5000);
		FormData fd_slider = new FormData();
		fd_slider.bottom = new FormAttachment(0, 72);
		fd_slider.right = new FormAttachment(100, -10);
		fd_slider.top = new FormAttachment(0, 11);
		fd_slider.left = new FormAttachment(0, 7);
		slider.setLayoutData(fd_slider);
		
		stepperPinch1 = new StepperPanel(grpPinchRoller, injector, StepperFunction.TopRoller);
		FormData fd_topPinch = new FormData();
		fd_topPinch.right = new FormAttachment(slider, 0, SWT.RIGHT);
		fd_topPinch.left = new FormAttachment(0, 10);
		fd_topPinch.bottom = new FormAttachment(0, 195);
		fd_topPinch.top = new FormAttachment(0, 136);
		stepperPinch1.setLayoutData(fd_topPinch);
		
		stepperPanel = new StepperPanel(grpPinchRoller, injector, StepperFunction.BottomRoller);
		FormData fd_stepperPanel = new FormData();
		fd_stepperPanel.right = new FormAttachment(stepperPinch1, 0, SWT.RIGHT);
		fd_stepperPanel.left = new FormAttachment(stepperPinch1, 0, SWT.LEFT);
		fd_stepperPanel.bottom = new FormAttachment(0, 284);
		fd_stepperPanel.top = new FormAttachment(0, 225);
		stepperPanel.setLayoutData(fd_stepperPanel);
		
		btnRunStop = new Button(grpPinchRoller, SWT.NONE);
		btnRunStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (isRunning) stop();
				else run();
			}
		});
		FormData fd_btnStart = new FormData();
		fd_btnStart.bottom = new FormAttachment(stepperPinch1, -17);
		fd_btnStart.top = new FormAttachment(slider, 12);
		fd_btnStart.left = new FormAttachment(slider, 0, SWT.LEFT);
		fd_btnStart.right = new FormAttachment(0, 73);
		btnRunStop.setLayoutData(fd_btnStart);
		btnRunStop.setText("Run");
		
		grpTakeupWheels = new Group(this, SWT.NONE);
		grpTakeupWheels.setText("Take-Up Wheels");
		grpTakeupWheels.setLayout(new FormLayout());
		FormData fd_grpTakeupWheels = new FormData();
		fd_grpTakeupWheels.bottom = new FormAttachment(0, 680);
		fd_grpTakeupWheels.right = new FormAttachment(grpPinchRoller, 0, SWT.RIGHT);
		fd_grpTakeupWheels.top = new FormAttachment(0, 350);
		fd_grpTakeupWheels.left = new FormAttachment(grpPinchRoller, 0, SWT.LEFT);
		grpTakeupWheels.setLayoutData(fd_grpTakeupWheels);
		
		pnlTopRoller = new TrackingStepperPanel(grpTakeupWheels, injector, StepperFunction.TopWheel, StepperFunction.TopRoller);
		FormData fd_topRoller = new FormData();
		fd_topRoller.bottom = new FormAttachment(0, 147);
		fd_topRoller.right = new FormAttachment(0, 795);
		fd_topRoller.top = new FormAttachment(0, 19);
		fd_topRoller.left = new FormAttachment(0, 7);
		pnlTopRoller.setLayoutData(fd_topRoller);
		
		pnlBtmRoller = new TrackingStepperPanel(grpTakeupWheels, injector, StepperFunction.BottomWheel, StepperFunction.TopWheel);
		FormData fd_btmRoller = new FormData();
		fd_btmRoller.bottom = new FormAttachment(0, 285);
		fd_btmRoller.right = new FormAttachment(0, 795);
		fd_btmRoller.top = new FormAttachment(0, 157);
		fd_btmRoller.left = new FormAttachment(0, 7);
		pnlBtmRoller.setLayoutData(fd_btmRoller);

		
		
		
		if (injector!=null) injector.injectMembers(this);
		
	}
	
	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
	}
	
	public void adjustSpeed() {
		int sliderVal = slider.getSelection();
		eb.post(new StepperSpeedChangeEvent(StepperFunction.TopRoller, sliderVal));
		eb.post(new StepperSpeedChangeEvent(StepperFunction.BottomRoller, sliderVal));
//		lblSliderValue.setText(""+sliderVal);
	}
	
	protected void run() {
		btnRunStop.setText("Stop");
		eb.post(new StepperRunEvent(StepperFunction.TopRoller));
		eb.post(new StepperRunEvent(StepperFunction.BottomRoller));
		isRunning  = true;
	}
	
	protected void stop() {
		btnRunStop.setText("Run");
		eb.post(new StepperStopEvent(StepperFunction.TopRoller));
		eb.post(new StepperStopEvent(StepperFunction.BottomRoller));
		isRunning = false;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
