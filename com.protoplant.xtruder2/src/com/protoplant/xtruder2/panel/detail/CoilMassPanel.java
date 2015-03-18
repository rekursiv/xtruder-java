package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.AudioManager;
import com.protoplant.xtruder2.ConversionManager;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.StepperConfigManager;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.CoilMassEvent;
import com.protoplant.xtruder2.event.CoilResetEvent;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperSpeedNudgeEvent;
import com.protoplant.xtruder2.event.StepperStopEvent;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;

import util.config.ConfigManager;

import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;


public class CoilMassPanel extends Group {
	
	private Logger log;
	private Label lblData;
	
	private volatile float delay=0;
	private volatile float grams=0;
	private volatile float prevGrams=0;
	
	private volatile boolean isMotorRunning = false;
	private volatile int curMotorSpeed = 0;
	
	private volatile long prevStepTime = 0;
//	private volatile float density=0;
	private volatile float diameter=0;
	private volatile float diameterUpdateCount=0;
	private volatile int fbCenterCount=0;
	private volatile int customMass=0;
	
	private Button rb125g;
	private Button rb1kg;
	private Button btnResetCoil;
	private Button btnResetCount;
	private Group grpMaterial;
	private Group grpReset;
	
//	private XtruderConfig config;
//	private AudioManager am;
//	private ConfigManager<XtruderConfig> cfgMgr;
	private Spinner spnCount;
	private Label lblCount;
	private EventBus eb;
//	private DataLogger dl;
	private Spinner spnDensity;
	private Group grpTargetDiameter;
	private Button btnFeedback;
	private Label lblFbPrevNudge;
	private Label lblFbCnt;
	private Button rb500g;
	private Button rbcg;
	private Spinner spnCustomMass;
	private XtruderConfig config;
	private ConversionManager convert;
	private AudioManager am;
	private StepperConfigManager scm;
	private Spinner spnTargetDia;


	public CoilMassPanel(Composite parent, Injector injector) {   //  350 x 327
		super(parent, SWT.NONE);
		
		setText("Coil Mass");
		setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		setLayout(new FormLayout());
		
		lblData = new Label(this, SWT.NONE);
		FormData fd_lblData = new FormData();
		fd_lblData.bottom = new FormAttachment(0, 62);
		fd_lblData.top = new FormAttachment(0, 8);
		fd_lblData.left = new FormAttachment(0, 7);
		lblData.setLayoutData(fd_lblData);
		lblData.setText("0.00 g");
		lblData.setFont(SWTResourceManager.getFont("Segoe UI", 24, SWT.NORMAL));
		
		btnResetCoil = new Button(this, SWT.NONE);
		btnResetCoil.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		btnResetCoil.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				resetCoil(false);
			}
		});
		FormData fd_btnResetMass = new FormData();
		fd_btnResetMass.left = new FormAttachment(lblData, 6);
		fd_btnResetMass.bottom = new FormAttachment(lblData, 49);
		fd_btnResetMass.top = new FormAttachment(lblData, 0, SWT.TOP);
		btnResetCoil.setLayoutData(fd_btnResetMass);
		btnResetCoil.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnResetCoil.setText("Reset Coil");
		
		grpMaterial = new Group(this, SWT.NONE);
		grpMaterial.setText("Density");
		FormData fd_grpMaterial = new FormData();
		fd_grpMaterial.left = new FormAttachment(0, 15);
		grpMaterial.setLayoutData(fd_grpMaterial);
		FillLayout fl_grpMaterial = new FillLayout(SWT.HORIZONTAL);
		fl_grpMaterial.marginWidth = 5;
		fl_grpMaterial.spacing = 4;
		grpMaterial.setLayout(fl_grpMaterial);
		
		grpReset = new Group(this, SWT.NONE);
		grpReset.setText("Target Mass");
		fd_grpMaterial.bottom = new FormAttachment(0, 295);
		fd_grpMaterial.top = new FormAttachment(grpReset, 6);
		fd_lblData.right = new FormAttachment(0, 180);
		grpReset.setLayout(null);
		FormData fd_grpReset = new FormData();
		fd_grpReset.left = new FormAttachment(0, 13);
		grpReset.setLayoutData(fd_grpReset);
		
		rb125g = new Button(grpReset, SWT.RADIO);
		rb125g.setBounds(8, 15, 62, 57);
		rb125g.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		rb125g.setText("125g");
		
		spnCount = new Spinner(this, SWT.BORDER);
		spnCount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
//				dl.write("Coil", ""+spnCount.getSelection(), String.format("%.2f", grams));
			}
		});

		fd_grpReset.bottom = new FormAttachment(spnCount, 81, SWT.BOTTOM);
		fd_grpReset.top = new FormAttachment(spnCount, 6);
		
		rb500g = new Button(grpReset, SWT.RADIO);
		rb500g.setBounds(79, 15, 62, 57);
		rb500g.setText("500g");
		rb500g.setSelection(true);
		rb500g.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		
		rb1kg = new Button(grpReset, SWT.RADIO);
		rb1kg.setBounds(152, 15, 77, 57);
		rb1kg.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		rb1kg.setText("1000g");
		
		rbcg = new Button(grpReset, SWT.RADIO);
		rbcg.setBounds(256, 15, 20, 57);

		rbcg.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		spnCount.setFont(SWTResourceManager.getFont("Segoe UI", 24, SWT.NORMAL));
		spnCount.setMaximum(1000);
		spnCount.setMinimum(1);
		FormData fd_spnCount = new FormData();
		fd_spnCount.bottom = new FormAttachment(0, 145);
		spnCount.setLayoutData(fd_spnCount);
		
		lblCount = new Label(this, SWT.NONE);
		lblCount.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		fd_spnCount.top = new FormAttachment(lblCount, 6);
		fd_spnCount.left = new FormAttachment(lblCount, 0, SWT.LEFT);
		FormData fd_lblCount = new FormData();
		fd_lblCount.right = new FormAttachment(0, 160);
		fd_lblCount.top = new FormAttachment(lblData);
		fd_lblCount.left = new FormAttachment(lblData, 0, SWT.LEFT);
		lblCount.setLayoutData(fd_lblCount);
		lblCount.setText("Coil Count");
		
		btnResetCount = new Button(this, SWT.NONE);
		fd_spnCount.right = new FormAttachment(btnResetCount, -6);
		fd_grpMaterial.right = new FormAttachment(100, -12);
		
		spnDensity = new Spinner(grpMaterial, SWT.BORDER);
		spnDensity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				config.conversion.density=(float)spnDensity.getSelection()/100.0f;
//				dl.write("Density", ""+density);
			}
		});
		spnDensity.setMaximum(800);
		spnDensity.setMinimum(20);
		spnDensity.setSelection(100);
		spnDensity.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		btnResetCount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				spnCount.setSelection(1);
//				dl.write("Coil", "1", String.format("%.2f", grams));
			}
		});
		btnResetCount.setText("Reset");
		btnResetCount.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		FormData fd_btnResetCount = new FormData();
		fd_btnResetCount.left = new FormAttachment(lblData, 6);
		fd_btnResetCount.right = new FormAttachment(0, 245);
		fd_btnResetCount.bottom = new FormAttachment(0, 135);
		fd_btnResetCount.top = new FormAttachment(spnCount, 0, SWT.TOP);
		btnResetCount.setLayoutData(fd_btnResetCount);
		
		grpTargetDiameter = new Group(this, SWT.NONE);
		fd_grpReset.right = new FormAttachment(grpTargetDiameter, -6);
		fd_btnResetMass.right = new FormAttachment(100, -234);
		grpTargetDiameter.setText("Target Diameter");
		grpTargetDiameter.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_grpTargetDiameter = new FormData();
		fd_grpTargetDiameter.right = new FormAttachment(100, -15);
		fd_grpTargetDiameter.bottom = new FormAttachment(grpReset, 75);
		fd_grpTargetDiameter.top = new FormAttachment(grpReset, 0, SWT.TOP);
		
		spnCustomMass = new Spinner(grpReset, SWT.BORDER);
		spnCustomMass.setPageIncrement(100);
		spnCustomMass.setIncrement(50);
		spnCustomMass.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				customMass=spnCustomMass.getSelection();
				log.info(""+customMass);
			}
		});
		spnCustomMass.setBounds(282, 15, 136, 57);
		spnCustomMass.setMaximum(3000);
		spnCustomMass.setMinimum(50);
		spnCustomMass.setSelection(750);
		spnCustomMass.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		fd_grpTargetDiameter.left = new FormAttachment(0, 445);
		grpTargetDiameter.setLayoutData(fd_grpTargetDiameter);
		
		btnFeedback = new Button(this, SWT.CHECK);
		btnFeedback.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!btnFeedback.getSelection()) {
					lblFbCnt.setText("-");
					lblFbPrevNudge.setText("-");
				}
			}
		});
		btnFeedback.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		FormData fd_btnFeedback = new FormData();
		fd_btnFeedback.right = new FormAttachment(100, -272);
		fd_btnFeedback.left = new FormAttachment(100, -411);
		btnFeedback.setLayoutData(fd_btnFeedback);
		btnFeedback.setText("Feedback");
		
		lblFbPrevNudge = new Label(this, SWT.NONE);
		fd_btnFeedback.top = new FormAttachment(lblFbPrevNudge, -22, SWT.TOP);
		fd_btnFeedback.bottom = new FormAttachment(lblFbPrevNudge);
		lblFbPrevNudge.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblFbPrevNudge.setText("-");
		FormData fd_lblFbPrevNudge = new FormData();
		fd_lblFbPrevNudge.right = new FormAttachment(100, -15);
		fd_lblFbPrevNudge.bottom = new FormAttachment(grpTargetDiameter, -6);
		
		spnTargetDia = new Spinner(grpTargetDiameter, SWT.BORDER);
		spnTargetDia.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				config.feedback.targetDiameter=(float)spnTargetDia.getSelection()/100.0f;
			}
		});
		spnTargetDia.setMaximum(300);
		spnTargetDia.setMinimum(150);
		spnTargetDia.setSelection(173);
		spnTargetDia.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		fd_lblFbPrevNudge.top = new FormAttachment(btnResetCount, -15);
		lblFbPrevNudge.setLayoutData(fd_lblFbPrevNudge);
		
		lblFbCnt = new Label(this, SWT.NONE);
		lblFbCnt.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		fd_lblFbPrevNudge.left = new FormAttachment(lblFbCnt, 0, SWT.LEFT);
		FormData fd_lblFbCnt = new FormData();
		fd_lblFbCnt.right = new FormAttachment(100, -15);
		fd_lblFbCnt.bottom = new FormAttachment(lblFbPrevNudge, -6);
		fd_lblFbCnt.top = new FormAttachment(btnResetCount, 0, SWT.TOP);
		fd_lblFbCnt.left = new FormAttachment(btnResetCoil, 6);
		lblFbCnt.setLayoutData(fd_lblFbCnt);
		lblFbCnt.setText("-");
		
		if (injector!=null) injector.injectMembers(this);

	}
	
	
	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, ConversionManager convert, StepperConfigManager scm, AudioManager am) {//, DataLogger dl
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.convert = convert;
		this.scm = scm;
//		this.dl = dl;
		this.am = am;
		
		this.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				isMotorRunning = false;
			}
		});
		customMass=spnCustomMass.getSelection();
	}

	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		spnDensity.setSelection((int)(config.conversion.density*100));
		spnTargetDia.setSelection((int)(config.feedback.targetDiameter*100));
	}

	@Subscribe
	public void onConfigStore(ConfigStoreEvent evt) {
		scm.storeConversionState(config.conversion.density);
		scm.storeFeedbackState(config.feedback.targetDiameter);
	}
	
	@Subscribe
	public void onMotorSpeedChange(StepperSpeedChangeEvent evt) {
		if (evt.getFunction()==StepperFunction.TopRoller) {
			curMotorSpeed = evt.getSpeed();
		}
	}
	
	@Subscribe
	public void onRun(StepperRunEvent evt) {
		if (evt.getFunction()==StepperFunction.TopRoller) {
			isMotorRunning = true;
		}
	}

	@Subscribe
	public void onStop(StepperStopEvent evt) {
		if (evt.getFunction()==StepperFunction.TopRoller) {
			isMotorRunning = false;
		}
	}
	
	@Subscribe
	public void onInidcatorData(final IndicatorDataEvent evt) {
		diameter = evt.getCur();

		if (isMotorRunning) {
			calcMass(diameter);
			if (diameterUpdateCount>config.feedback.period) {
				diameterUpdateCount=0;
				if (btnFeedback.getSelection()) doFeedback();
			} else {
				++diameterUpdateCount;
			}
		}
	}
	
	public void doFeedback() {
		float delta = diameter-config.feedback.targetDiameter;
		if (Math.abs(delta)<config.feedback.deadband) {
			++fbCenterCount;
		} else {
			fbCenterCount=0;
			if (delta>config.feedback.spread) delta=config.feedback.spread;
			else if (delta<-config.feedback.spread) delta=-config.feedback.spread;
			delta*=config.feedback.nudgeFactor;
			eb.post(new StepperSpeedNudgeEvent(StepperFunction.TopRoller, (int)delta));
		}
		lblFbPrevNudge.setText(String.format("Delta: "+(int)delta));
		lblFbCnt.setText("DBCount:  "+fbCenterCount);
	}
	
	private void calcMass(float diameter) {
		delay = (System.currentTimeMillis()-prevStepTime)/1000.0f;  // convert to seconds
		prevStepTime=System.currentTimeMillis();
		if (delay>1) return;
		
		grams+=convert.toGrams(diameter, delay, curMotorSpeed);
		
		if (rb125g.getSelection()) {
			if (grams>125) resetCoil(true);
			updateAudio(125);
		}
		else if (rb500g.getSelection()) {
			if (grams>500) resetCoil(true);
			updateAudio(500);
		}
		else if (rb1kg.getSelection()) {
			if (grams>1000) resetCoil(true);
			updateAudio(1000);
		}
		else if (rbcg.getSelection()) {
			if (grams>customMass) resetCoil(true);
			updateAudio(customMass);
		}
		
		lblData.setText(String.format("%.2f g", grams));
		prevGrams = grams;

		eb.post(new CoilMassEvent(grams));
	}
	
	private void resetCoil(boolean isWrapAround) {
//		dl.write("Coil", "RESET", ""+spnCount.getSelection(), String.format("%.2f", grams));
		grams=0;
//		grams=440;                             //////////////////////////////////////////////////////////////////////      TEST
		if (isWrapAround) {
			incrementCount();
			am.speak("mark");
		} else {
			lblData.setText("0.00 g");
		}
		eb.post(new CoilResetEvent(isWrapAround));
	}

	private void updateAudio(int total) {
		if (checkpoint(total-50)) am.speak("50 grams to go");
		else if (checkpoint(total-30)) am.speak("30 grams to go");
		else if (checkpoint(total-10)) am.speak("10 grams to go");
		else if (checkpoint(total-5)) am.speak("5");
		else if (checkpoint(total-4)) am.speak("4");
		else if (checkpoint(total-3)) am.speak("3");
		else if (checkpoint(total-2)) am.speak("2");
		else if (checkpoint(total-1)) am.speak("1");
	}
	
	private boolean checkpoint(float ref) {
		if (ref==0 && (grams+10)<prevGrams) return true;
		else if (ref!=0 && prevGrams<=ref && grams>=ref) return true;
		else return false;
	}
	
	private void incrementCount() {
		spnCount.setSelection(spnCount.getSelection()+1);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
