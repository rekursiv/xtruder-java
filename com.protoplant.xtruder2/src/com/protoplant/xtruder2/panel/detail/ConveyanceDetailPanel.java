package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperSpeedNudgeEvent;
import com.protoplant.xtruder2.event.StepperStopEvent;
import com.protoplant.xtruder2.panel.StepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
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
	protected Scale sldSpeedAdjust;
	protected StepperPanel pnlTopRoller;
	protected StepperPanel pnlBtmRoller;
	protected Button btnRunStop;
	protected Group grpTakeupWheels;
	protected TrackingStepperPanel pnlTopWheel;
	protected TrackingStepperPanel pnlBtmWheel;
	private boolean isRunning = false;
	private XtruderConfig config;
	private Button btnLeft;
	private Button btnRight;

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
		
		sldSpeedAdjust = new Scale(grpPinchRoller, SWT.NONE);
		sldSpeedAdjust.setTouchEnabled(true);
		sldSpeedAdjust.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				adjustSpeed();
			}
		});
		sldSpeedAdjust.setPageIncrement(20);

		FormData fd_slider = new FormData();
		fd_slider.bottom = new FormAttachment(0, 75);
		fd_slider.top = new FormAttachment(0, 11);
		sldSpeedAdjust.setLayoutData(fd_slider);
		
		pnlTopRoller = new StepperPanel(grpPinchRoller, injector, StepperFunction.TopRoller);
		FormData fd_topPinch = new FormData();
		fd_topPinch.right = new FormAttachment(sldSpeedAdjust, 0, SWT.RIGHT);
		fd_topPinch.left = new FormAttachment(0, 10);
		fd_topPinch.bottom = new FormAttachment(0, 195);
		fd_topPinch.top = new FormAttachment(0, 136);
		pnlTopRoller.setLayoutData(fd_topPinch);
		
		pnlBtmRoller = new StepperPanel(grpPinchRoller, injector, StepperFunction.BottomRoller);
		FormData fd_stepperPanel = new FormData();
		fd_stepperPanel.right = new FormAttachment(pnlTopRoller, 0, SWT.RIGHT);
		fd_stepperPanel.left = new FormAttachment(pnlTopRoller, 0, SWT.LEFT);
		fd_stepperPanel.bottom = new FormAttachment(0, 284);
		fd_stepperPanel.top = new FormAttachment(0, 225);
		pnlBtmRoller.setLayoutData(fd_stepperPanel);
		
		btnRunStop = new Button(grpPinchRoller, SWT.NONE);
		btnRunStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (isRunning) stop();
				else run();
			}
		});
		FormData fd_btnStart = new FormData();
		fd_btnStart.bottom = new FormAttachment(pnlTopRoller, -17);
		fd_btnStart.top = new FormAttachment(sldSpeedAdjust, 12);
		fd_btnStart.left = new FormAttachment(0, 10);
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
		
		btnLeft = new Button(grpPinchRoller, SWT.NONE);
		btnLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				sldSpeedAdjust.setSelection(sldSpeedAdjust.getSelection()-1);
				adjustSpeed();
			}
		});
		fd_slider.left = new FormAttachment(btnLeft);
		btnLeft.setTouchEnabled(true);
		btnLeft.setText("<");
		FormData fd_btnLeft = new FormData();
		fd_btnLeft.bottom = new FormAttachment(0, 73);
		fd_btnLeft.right = new FormAttachment(0, 38);
		fd_btnLeft.top = new FormAttachment(sldSpeedAdjust, 0, SWT.TOP);
		fd_btnLeft.left = new FormAttachment(btnRunStop, 0, SWT.LEFT);
		btnLeft.setLayoutData(fd_btnLeft);
		
		btnRight = new Button(grpPinchRoller, SWT.NONE);
		btnRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				sldSpeedAdjust.setSelection(sldSpeedAdjust.getSelection()+1);
				adjustSpeed();
			}
		});
		fd_slider.right = new FormAttachment(btnRight);
		btnRight.setTouchEnabled(true);
		btnRight.setText(">");
		FormData fd_btnRight = new FormData();
		fd_btnRight.bottom = new FormAttachment(sldSpeedAdjust, 62);
		fd_btnRight.top = new FormAttachment(sldSpeedAdjust, 0, SWT.TOP);
		fd_btnRight.right = new FormAttachment(100, -8);
		fd_btnRight.left = new FormAttachment(100, -33);
		btnRight.setLayoutData(fd_btnRight);
		grpTakeupWheels.setLayoutData(fd_grpTakeupWheels);
		
		pnlTopWheel = new TrackingStepperPanel(grpTakeupWheels, injector, StepperFunction.TopWheel, StepperFunction.TopRoller);
		FormData fd_topRoller = new FormData();
		fd_topRoller.bottom = new FormAttachment(0, 147);
		fd_topRoller.right = new FormAttachment(100, -10);
		fd_topRoller.top = new FormAttachment(0, 19);
		fd_topRoller.left = new FormAttachment(0, 7);
		pnlTopWheel.setLayoutData(fd_topRoller);
		
		pnlBtmWheel = new TrackingStepperPanel(grpTakeupWheels, injector, StepperFunction.BottomWheel, StepperFunction.TopWheel);
		FormData fd_btmRoller = new FormData();
		fd_btmRoller.bottom = new FormAttachment(0, 285);
		fd_btmRoller.right = new FormAttachment(100, -10);
		fd_btmRoller.top = new FormAttachment(0, 157);
		fd_btmRoller.left = new FormAttachment(0, 7);
		pnlBtmWheel.setLayoutData(fd_btmRoller);

		
		if (injector!=null) injector.injectMembers(this);
		
	}
	
	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		log.info("");
	}
	
	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		sldSpeedAdjust.setMaximum(config.conveyance.speedSliderMax);
		sldSpeedAdjust.setMinimum(config.conveyance.speedSliderMin);
		sldSpeedAdjust.setSelection(config.conveyance.speedSliderInit);
		adjustSpeed();
//		log.info(""+config.conveyance.speedSliderMin);
	}
	
	@Subscribe
	public void onConfigStore(ConfigStoreEvent evt) {
		config.conveyance.speedSliderInit = sldSpeedAdjust.getSelection();
	}
	
	@Subscribe
	public void onNudge(StepperSpeedNudgeEvent evt) {
		sldSpeedAdjust.setSelection(sldSpeedAdjust.getSelection()+evt.getDelta());
		adjustSpeed();
	}
	
	public void adjustSpeed() {
		pnlTopRoller.setSpeed(sldSpeedAdjust.getSelection());
		pnlBtmRoller.setSpeed(sldSpeedAdjust.getSelection());
	}
	
	protected void run() {
		btnRunStop.setText("Stop");
		pnlTopRoller.run();
		pnlBtmRoller.run();
		isRunning  = true;
	}
	
	protected void stop() {
		btnRunStop.setText("Run");
		pnlTopRoller.stop();
		pnlBtmRoller.stop();
		isRunning = false;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
