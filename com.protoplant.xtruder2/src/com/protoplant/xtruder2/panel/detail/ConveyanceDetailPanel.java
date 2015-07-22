package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.nebula.visualization.widgets.figures.ScaledSliderFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.MachineState;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;
import com.protoplant.xtruder2.event.StepperSpeedNudgeEvent;
import com.protoplant.xtruder2.panel.StepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;

public class ConveyanceDetailPanel extends Composite {

	private Logger log;
	private EventBus eb;
	protected Group grpPinchRoller;
	private Canvas cvsSpeedAdjust;
	private ScaledSliderFigure sldSpeedAdjust;
	private LightweightSystem lws;
	protected StepperPanel pnlTopRoller;
	protected StepperPanel pnlBtmRoller;
	protected Button btnRunStop;
	protected Group grpTakeupWheels;
	protected TrackingStepperPanel pnlTopWheel;
	protected TrackingStepperPanel pnlBtmWheel;
	private boolean isRunning = false;
	private XtruderConfig config;
	private MachineState ms;
	
	
	
/*
 		cvsSpeedAdjust = new Canvas(this, SWT.NONE);
		FormData fd_canvas = new FormData();
		cvsSpeedAdjust.setLayoutData(fd_canvas);
		lws = new LightweightSystem(cvsSpeedAdjust);
		lws.setContents(slider);
 */
	public ConveyanceDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FormLayout());
		
		grpPinchRoller = new Group(this, SWT.NONE);
		FormData fd_grpPinchRoller = new FormData();
		fd_grpPinchRoller.bottom = new FormAttachment(0, 271);
		fd_grpPinchRoller.right = new FormAttachment(100, -10);
		fd_grpPinchRoller.top = new FormAttachment(0, 10);
		fd_grpPinchRoller.left = new FormAttachment(0, 10);
		grpPinchRoller.setLayoutData(fd_grpPinchRoller);
		grpPinchRoller.setText("Pinch Rollers");
		grpPinchRoller.setLayout(new FormLayout());
	
		pnlTopRoller = new StepperPanel(grpPinchRoller, injector, StepperFunction.TopRoller);
		FormData fd_topPinch = new FormData();
		pnlTopRoller.setLayoutData(fd_topPinch);
		
		pnlBtmRoller = new StepperPanel(grpPinchRoller, injector, StepperFunction.BottomRoller);
		FormData fd_stepperPanel = new FormData();
		fd_stepperPanel.bottom = new FormAttachment(pnlTopRoller, 59, SWT.BOTTOM);
		fd_stepperPanel.top = new FormAttachment(pnlTopRoller);
		fd_stepperPanel.right = new FormAttachment(100, -18);
		fd_stepperPanel.left = new FormAttachment(pnlTopRoller, 0, SWT.LEFT);
		pnlBtmRoller.setLayoutData(fd_stepperPanel);
		
		btnRunStop = new Button(grpPinchRoller, SWT.NONE);
		fd_topPinch.bottom = new FormAttachment(btnRunStop, 65, SWT.BOTTOM);
		fd_topPinch.top = new FormAttachment(btnRunStop, 6);
		fd_topPinch.right = new FormAttachment(100, -15);
		fd_topPinch.left = new FormAttachment(btnRunStop, 0, SWT.LEFT);
		btnRunStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (isRunning) stop();
				else run();
			}
		});
		FormData fd_btnStart = new FormData();
		fd_btnStart.bottom = new FormAttachment(0, 110);
		fd_btnStart.top = new FormAttachment(0, 80);
		fd_btnStart.left = new FormAttachment(0, 10);
		fd_btnStart.right = new FormAttachment(0, 73);
		btnRunStop.setLayoutData(fd_btnStart);
		btnRunStop.setText("Run");
		
		grpTakeupWheels = new Group(this, SWT.NONE);
		grpTakeupWheels.setText("Take-Up Wheels");
		grpTakeupWheels.setLayout(new FormLayout());
		FormData fd_grpTakeupWheels = new FormData();
		fd_grpTakeupWheels.bottom = new FormAttachment(grpPinchRoller, 460, SWT.BOTTOM);
		fd_grpTakeupWheels.top = new FormAttachment(grpPinchRoller, 11);
		fd_grpTakeupWheels.right = new FormAttachment(100, -10);
		fd_grpTakeupWheels.left = new FormAttachment(grpPinchRoller, 0, SWT.LEFT);
		
		cvsSpeedAdjust = new Canvas(grpPinchRoller, SWT.NONE);
		FormData fd_cvsSpeedAdjust = new FormData();
		fd_cvsSpeedAdjust.bottom = new FormAttachment(0, 80);
		fd_cvsSpeedAdjust.left = new FormAttachment(0, 12);
		fd_cvsSpeedAdjust.right = new FormAttachment(100, -12);
		fd_cvsSpeedAdjust.top = new FormAttachment(0, 0);
		cvsSpeedAdjust.setLayoutData(fd_cvsSpeedAdjust);

		grpTakeupWheels.setLayoutData(fd_grpTakeupWheels);
		
		pnlTopWheel = new TrackingStepperPanel(grpTakeupWheels, injector, StepperFunction.TopWheel, StepperFunction.TopRoller);
		FormData fd_topWheel = new FormData();
		fd_topWheel.bottom = new FormAttachment(0, 210);
		fd_topWheel.top = new FormAttachment(0, 10);
		pnlTopWheel.setLayoutData(fd_topWheel);
		
		pnlBtmWheel = new TrackingStepperPanel(grpTakeupWheels, injector, StepperFunction.BottomWheel, StepperFunction.TopWheel);
		fd_topWheel.right = new FormAttachment(100, -8);
		fd_topWheel.left = new FormAttachment(0, 12);
		FormData fd_btmWheel = new FormData();
		fd_btmWheel.bottom = new FormAttachment(100, -19);
		fd_btmWheel.top = new FormAttachment(pnlTopWheel, 6);
		fd_btmWheel.right = new FormAttachment(100, -13);
		fd_btmWheel.left = new FormAttachment(0, 12);
		pnlBtmWheel.setLayoutData(fd_btmWheel);
		
		
		
		sldSpeedAdjust = new ScaledSliderFigure();
		lws = new LightweightSystem(cvsSpeedAdjust);
		lws.setContents(sldSpeedAdjust);

//		sldSpeedAdjust.setEffect3D(false);
		sldSpeedAdjust.setFillColor(new Color(cvsSpeedAdjust.getDisplay(), 230, 80, 0));
		sldSpeedAdjust.setHorizontal(true);
		sldSpeedAdjust.setShowMarkers(false);
		sldSpeedAdjust.setShowLo(false);
		sldSpeedAdjust.setShowLolo(false);
		sldSpeedAdjust.setShowHi(false);
		sldSpeedAdjust.setShowHihi(false);
		sldSpeedAdjust.setPageIncrement(100);
		sldSpeedAdjust.setMajorTickMarkStepHint(100);
		sldSpeedAdjust.setValueLabelFormat("0");
		sldSpeedAdjust.getScale().setFormatPattern("0");
		sldSpeedAdjust.addManualValueChangeListener(new IManualValueChangeListener() {
			public void manualValueChanged(double newValue) {
//				System.out.println("You set value to: " + newValue);
				adjustSpeed();
			}
		});

		grpPinchRoller.setTabList(new Control[]{cvsSpeedAdjust});
		
		if (injector!=null) injector.injectMembers(this);
		
	}
	
	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, MachineState ms) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.ms = ms;

	}
	
	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		sldSpeedAdjust.setRange(config.conveyance.speedSliderMin, config.conveyance.speedSliderMax);
		sldSpeedAdjust.setValue(config.conveyance.speedSliderInit);
		adjustSpeed();
//		log.info(""+config.conveyance.speedSliderMin);
	}
	
	@Subscribe
	public void onConfigStore(ConfigStoreEvent evt) {
		ms.conveyance.speedSliderInit = (int)sldSpeedAdjust.getValue();
	}
	
	@Subscribe
	public void onNudge(StepperSpeedNudgeEvent evt) {
		sldSpeedAdjust.setValue(sldSpeedAdjust.getValue()+evt.getDelta());
		adjustSpeed();
	}
	
	public void adjustSpeed() {
//		getValue()
		pnlTopRoller.setSpeed((int)sldSpeedAdjust.getValue());
		pnlBtmRoller.setSpeed((int)sldSpeedAdjust.getValue());
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
