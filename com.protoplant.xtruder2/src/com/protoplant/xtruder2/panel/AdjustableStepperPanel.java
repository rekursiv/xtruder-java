package com.protoplant.xtruder2.panel;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.nebula.visualization.widgets.figures.ScaledSliderFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.event.CoilResetEvent;
import com.protoplant.xtruder2.event.CoilResetEvent.Context;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;

public class AdjustableStepperPanel extends StepperPanel {
	

	private LightweightSystem lws;
	protected Canvas cvsSpeedAdjust;
	protected ScaledSliderFigure sldSpeedAdjust;
	
	protected Button btnRunStop;
	private Label lblPosition;

	public AdjustableStepperPanel(Composite parent, Injector injector, StepperFunction type) {
		super(parent, null, type);

		btnRunStop = new Button(this, SWT.NONE);
		FormData fd_btnRunStop = new FormData();
		fd_btnRunStop.bottom = new FormAttachment(0, 165);
		fd_btnRunStop.top = new FormAttachment(0, 125);
		btnRunStop.setLayoutData(fd_btnRunStop);
		btnRunStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (isRunning) stop();
				else run();
			}
		});
		btnRunStop.setText("Run");
		
		lblPosition = new Label(this, SWT.NONE);
		FormData fd_lblPosition = new FormData();
		fd_lblPosition.right = new FormAttachment(lblSetpt, 273);
		fd_lblPosition.left = new FormAttachment(lblSetpt, 0, SWT.LEFT);
		fd_lblPosition.bottom = new FormAttachment(0, 238);
		fd_lblPosition.top = new FormAttachment(0, 203);
		lblPosition.setLayoutData(fd_lblPosition);
		lblPosition.setText("Position:");
		
		cvsSpeedAdjust = new Canvas(this, SWT.NONE);
		fd_btnRunStop.right = new FormAttachment(100, -827);
		fd_btnRunStop.left = new FormAttachment(0, 18);
		FormData fd_cvsSpeedAdjust = new FormData();
		fd_cvsSpeedAdjust.bottom = new FormAttachment(0, 123);
		fd_cvsSpeedAdjust.top = new FormAttachment(0, 28);
		fd_cvsSpeedAdjust.right = new FormAttachment(100, -6);
		fd_cvsSpeedAdjust.left = new FormAttachment(0, 15);
		cvsSpeedAdjust.setLayoutData(fd_cvsSpeedAdjust);
		
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
				adjustSpeed();
			}
		});
		
	    setTabList(new Control[]{cvsSpeedAdjust});

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
		sldSpeedAdjust.setRange(scm.getConfig(function).speedSliderMin, scm.getConfig(function).speedSliderMax);
		sldSpeedAdjust.setValue(scm.getConfig(function).speedSliderInit);
		adjustSpeed();
	}
	
	@Subscribe
	public void onConfigStore(ConfigStoreEvent evt) {
		scm.storeStepperState(function, (int)sldSpeedAdjust.getValue());
	}
	
	@Override
	protected void adjustSpeed() {
		speed = (int)sldSpeedAdjust.getValue();
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
		
		//  "remote spool reset" hack
		if (function==StepperFunction.Winder && position==2) {
			eb.post(new CoilResetEvent(Context.RESET));
//			log.info(""+position);
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
