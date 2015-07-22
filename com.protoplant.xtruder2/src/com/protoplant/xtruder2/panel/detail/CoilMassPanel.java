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
import org.eclipse.swt.widgets.Control;
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
import com.protoplant.xtruder2.config.MachineState;
import com.protoplant.xtruder2.config.StepperConfigManager;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.CoilMassEvent;
import com.protoplant.xtruder2.event.CoilResetEvent;
import com.protoplant.xtruder2.event.CoilResetEvent.Context;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.ProductionModeChangeEvent;
import com.protoplant.xtruder2.event.SpoolTargetMassEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperSpeedNudgeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;
import com.protoplant.xtruder2.event.StepperStopEvent;
import com.protoplant.xtruder2.panel.summary.ProductionPanel.Mode;

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
	
	private volatile float delay=0;
	private volatile float grams=0;
	private volatile float prevGrams=0;
	
	private volatile boolean isEnabled = false;
	private volatile int curMotorSpeed = 0;
	
	private volatile long prevStepTime = 0;
	private volatile float diameter=0;

	private volatile int spoolTargetMass=500;
	
	
	private Button rb125g;
	private Button rb2kg;
	private Button btnResetCount;
	private Group grpMaterial;
	private Group grpReset;
	
	private Spinner spnCount;
	private EventBus eb;
	private Spinner spnDensity;
	private Button rb500g;
	private Button rbcg;
	private Spinner spnCustomMass;
	private XtruderConfig config;
	private ConversionManager convert;
	private AudioManager am;
	private MachineState ms;


	public CoilMassPanel(Composite parent, Injector injector) {   //  350 x 327
		super(parent, SWT.NONE);
		
		setText("Coil Mass");
		setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		setLayout(null);
		
		grpMaterial = new Group(this, SWT.NONE);
		grpMaterial.setBounds(328, 46, 216, 118);
		grpMaterial.setText("Density");
		
		grpReset = new Group(this, SWT.NONE);
		grpReset.setBounds(38, 170, 506, 99);
		grpReset.setText("Target Mass");
		grpReset.setLayout(null);
		
		rb125g = new Button(grpReset, SWT.RADIO);
		rb125g.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (rb125g.getSelection()) eb.post(new SpoolTargetMassEvent(125));
			}
		});
		rb125g.setBounds(8, 15, 62, 57);
		rb125g.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		rb125g.setText("125g");
		
		rb500g = new Button(grpReset, SWT.RADIO);
		rb500g.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (rb500g.getSelection()) eb.post(new SpoolTargetMassEvent(500));
			}
		});
		rb500g.setBounds(79, 15, 62, 57);
		rb500g.setText("500g");
		rb500g.setSelection(true);
		rb500g.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		
		rb2kg = new Button(grpReset, SWT.RADIO);
		rb2kg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (rb2kg.getSelection()) eb.post(new SpoolTargetMassEvent(2000));	
			}
		});
		rb2kg.setBounds(152, 15, 77, 57);
		rb2kg.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		rb2kg.setText("2000g");
		
		rbcg = new Button(grpReset, SWT.RADIO);
		rbcg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (rbcg.getSelection()) eb.post(new SpoolTargetMassEvent(spnCustomMass.getSelection()));
			}
		});
		rbcg.setBounds(251, 15, 20, 57);

		rbcg.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		grpMaterial.setLayout(null);
		
		spnDensity = new Spinner(grpMaterial, SWT.BORDER);
		spnDensity.setBounds(10, 25, 152, 48);
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
		
		spnCustomMass = new Spinner(grpReset, SWT.BORDER);
		spnCustomMass.setPageIncrement(100);
		spnCustomMass.setIncrement(50);
		spnCustomMass.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eb.post(new SpoolTargetMassEvent(spnCustomMass.getSelection()));
			}
		});
		spnCustomMass.setBounds(274, 22, 136, 41);
		spnCustomMass.setMaximum(3000);
		spnCustomMass.setMinimum(50);
		spnCustomMass.setSelection(750);
		spnCustomMass.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		
		Group grpCoilCount = new Group(this, SWT.NONE);
		grpCoilCount.setText("Coil Count");
		grpCoilCount.setBounds(40, 46, 282, 118);
		
		spnCount = new Spinner(grpCoilCount, SWT.BORDER);
		spnCount.setBounds(10, 29, 173, 46);
		spnCount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
//				dl.write("Coil", ""+spnCount.getSelection(), String.format("%.2f", grams));
			}
		});
		spnCount.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		spnCount.setMaximum(1000);
		spnCount.setMinimum(1);
		
		btnResetCount = new Button(grpCoilCount, SWT.NONE);
		btnResetCount.setBounds(189, 29, 83, 46);
		btnResetCount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				spnCount.setSelection(1);
//				dl.write("Coil", "1", String.format("%.2f", grams));
			}
		});
		btnResetCount.setText("Reset");
		btnResetCount.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		
		setTabList(new Control[]{});
		
		if (injector!=null) injector.injectMembers(this);

	}
	
	
	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, ConversionManager convert, MachineState ms, AudioManager am) {//, DataLogger dl
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.convert = convert;
		this.ms = ms;
		this.am = am;
		
		eb.post(new SpoolTargetMassEvent(500));
	}

	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		spnDensity.setSelection((int)(config.conversion.density*100));
	}

	@Subscribe
	public void onConfigStore(ConfigStoreEvent evt) {
		ms.conversion.density = config.conversion.density;
	}
	
	@Subscribe
	public void onProductionModeChange(final ProductionModeChangeEvent evt) {
		if (evt.getMode()==Mode.EXTRUDE) {
			isEnabled=true;
		} else {
			isEnabled=false;
			eb.post(new CoilResetEvent(Context.SILENT));
		}
	}
	
	@Subscribe
	public void onStepperStatus(final StepperStatusEvent evt) {
		if (evt.getFunction()==StepperFunction.TopRoller) {
			curMotorSpeed = evt.getSpeed();
		}
	}
	
	
	@Subscribe
	public void onInidcatorData(final IndicatorDataEvent evt) {
		diameter = evt.getCur();
		if (curMotorSpeed!=0 && isEnabled) {
			calcMass(diameter);
		}
	}
	
	@Subscribe
	public void onCoilReset(CoilResetEvent evt) {
		grams=0;
		eb.post(new CoilMassEvent(grams));
		
		if (evt.getContext()==Context.WRAP) {
			incrementCount();
			am.playClip("mark");
		} else if (evt.getContext()==Context.RESET) {
			am.playClip("reset");
		}
//		dl.write("Coil", "RESET", ""+spnCount.getSelection(), String.format("%.2f", grams));
	}
	
	@Subscribe
	public void onSpoolTargetMass(SpoolTargetMassEvent evt) {
		spoolTargetMass=evt.getMass();
	}

	private void calcMass(float diameter) {
		delay = (System.currentTimeMillis()-prevStepTime)/1000.0f;  // convert to seconds
		prevStepTime=System.currentTimeMillis();
		if (delay>1) return;
		
		grams+=convert.toGrams(diameter, delay, curMotorSpeed);
		
		if (grams>spoolTargetMass) eb.post(new CoilResetEvent(Context.WRAP));
		updateAudio();
		
		prevGrams = grams;
		eb.post(new CoilMassEvent(grams));
	}
	
	private void updateAudio() {
		if (checkpoint(spoolTargetMass-50)) am.playClip("50gtg");
		else if (checkpoint(spoolTargetMass-30)) am.playClip("30gtg");
		else if (checkpoint(spoolTargetMass-10)) am.playClip("10gtg");
		else if (checkpoint(spoolTargetMass-5)) am.playClip("5");
		else if (checkpoint(spoolTargetMass-4)) am.playClip("4");
		else if (checkpoint(spoolTargetMass-3)) am.playClip("3");
		else if (checkpoint(spoolTargetMass-2)) am.playClip("2");
		else if (checkpoint(spoolTargetMass-1)) am.playClip("1");
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
