package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.AudioManager;
import com.protoplant.xtruder2.FeederMonitor;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.AnalogDataEvent;
import com.protoplant.xtruder2.event.CoilResetEvent;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.ProductionModeChangeEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.panel.AdjustableStepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;
import com.protoplant.xtruder2.panel.summary.ProductionPanel.Mode;
import com.protoplant.xtruder2.test.Test;
import com.protoplant.xtruder2.usb.UsbManager;

public class AlarmPanel extends Group {

	private int hopperFullCount;
	private int hopperEmptyCount;
	private int hopperDisconnectCount;
	private int hopperSilenceCount;
	private int diaSilenceCount;
	private int diaRepeatCount;
	private int diaResetCount;
	private int diaOverCount;
	private int diaUnderCount;
	private int usbEventHz;
	private boolean needsReset;
	
	private Logger log;
	private EventBus eb;
	private XtruderConfig config;
	protected AdjustableStepperPanel asp;
	protected TrackingStepperPanel tsp;
	protected Composite composite;
	protected Label lblHopperData;
	private AudioManager am;
	
	private float curDiaMin=0;
	private float curDiaMax=0;
	private Label lblPrevMax;
	private Label lblPrevMin;
	private Label lblMax;
	private Label lblMin;
	private Button btnResetDia;
	private Label lblOver;
	private Label lblUnder;
	private Button chbDiaSilence;
	private Button chbHopperSilence;
	private Group grpPressure;
	private Label lblPresMax;
	private Button chbPresSilence;
	private int pressureSilenceCount;
	private int pressureAlarmCount;
	private Button chbMonitorFeeders;
	private FeederMonitor fm1;
	
	public AlarmPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		setText("Alarms");
		
		Group grpDiameterMinmax = new Group(this, SWT.NONE);
		grpDiameterMinmax.setText("Diameter");
		grpDiameterMinmax.setBounds(10,  10, 329, 153);
		
		lblPrevMax = new Label(grpDiameterMinmax, SWT.BORDER);
		lblPrevMax.setBounds(99, 10, 100, 25);
		
		lblPrevMin = new Label(grpDiameterMinmax, SWT.BORDER);
		lblPrevMin.setBounds(99, 41, 100, 25);
		
		lblMax = new Label(grpDiameterMinmax, SWT.BORDER);
		lblMax.setBounds(219, 10, 100, 25);
		
		lblMin = new Label(grpDiameterMinmax, SWT.BORDER);
		lblMin.setBounds(219, 41, 100, 25);
		
		btnResetDia = new Button(grpDiameterMinmax, SWT.NONE);
		btnResetDia.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				resetDia();
			}
		});
		btnResetDia.setBounds(10, 104, 100, 25);
		btnResetDia.setText("Reset");
		
		Label lblOverTitle = new Label(grpDiameterMinmax, SWT.NONE);
		lblOverTitle.setBounds(10, 73, 47, 25);
		lblOverTitle.setText("# Over:");
		
		Label lblUnderTitle = new Label(grpDiameterMinmax, SWT.NONE);
		lblUnderTitle.setText("# Under:");
		lblUnderTitle.setBounds(148, 73, 55, 25);
		
		lblOver = new Label(grpDiameterMinmax, SWT.BORDER);
		lblOver.setBounds(63, 72, 55, 25);
		
		lblUnder = new Label(grpDiameterMinmax, SWT.BORDER);
		lblUnder.setBounds(209, 72, 57, 25);
		
		chbDiaSilence = new Button(grpDiameterMinmax, SWT.CHECK);
		chbDiaSilence.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (chbDiaSilence.getSelection()) {
					diaSilenceCount=config.alarm.diaAlarmSilenceSeconds*usbEventHz;
				} else {
					chbDiaSilence.setText("Silence");
					diaSilenceCount=0;
				}
			}
		});
		chbDiaSilence.setBounds(130, 101, 136, 30);
		chbDiaSilence.setText("Silence");
		
		Label lblMax_1 = new Label(grpDiameterMinmax, SWT.NONE);
		lblMax_1.setBounds(30, 11, 55, 15);
		lblMax_1.setText("Max:");
		
		Label lblMin_1 = new Label(grpDiameterMinmax, SWT.NONE);
		lblMin_1.setBounds(30, 42, 55, 15);
		lblMin_1.setText("Min:");
		
		Group grpHopper = new Group(this, SWT.NONE);
		grpHopper.setText("Hopper");
		grpHopper.setBounds(10, 169, 155, 93);
		
		lblHopperData = new Label(grpHopper, SWT.BORDER);
		lblHopperData.setBounds(19, 10, 115, 23);
		lblHopperData.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		
		chbHopperSilence = new Button(grpHopper, SWT.CHECK);
		chbHopperSilence.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (chbHopperSilence.getSelection()) {
					hopperSilenceCount=config.alarm.hopperAlarmSilenceSeconds*usbEventHz;
				} else {
					chbHopperSilence.setText("Silence");
					hopperSilenceCount=0;
				}
			}
		});
		chbHopperSilence.setText("Silence");
		chbHopperSilence.setBounds(29, 39, 115, 23);
		
		chbMonitorFeeders = new Button(grpHopper, SWT.CHECK);
		chbMonitorFeeders.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (chbMonitorFeeders.getSelection()) {
					fm1.connect("192.168.1.11");
				} else {
					fm1.disconnect();
				}
			}
		});
		chbMonitorFeeders.setBounds(19, 67, 115, 16);
		chbMonitorFeeders.setText("Monitor Feeders");
		
		grpPressure = new Group(this, SWT.NONE);
		grpPressure.setText("Pressure");
		grpPressure.setBounds(171, 169, 168, 93);
		
		lblPresMax = new Label(grpPressure, SWT.BORDER);
		lblPresMax.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblPresMax.setBounds(16, 10, 124, 24);
		
		chbPresSilence = new Button(grpPressure, SWT.CHECK);
		chbPresSilence.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (chbPresSilence.getSelection()) {
					pressureSilenceCount=config.alarm.pressureAlarmSilenceSeconds*usbEventHz;
				} else {
					chbPresSilence.setText("Silence");
					pressureSilenceCount=0;
				}
			}
		});
		chbPresSilence.setText("Silence");
		chbPresSilence.setBounds(26, 40, 115, 24);
		
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, AudioManager am, FeederMonitor fm1) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.am = am;
		this.fm1 = fm1;
		
		diaOverCount=0;
		diaUnderCount=0;
		diaResetCount=-1;
		needsReset=false;
		lblOver.setText(""+diaOverCount);
		lblUnder.setText(""+diaUnderCount);
		usbEventHz=1000/UsbManager.IO_REFRESH_PERIOD;
	}

	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		lblPresMax.setText("MAX: "+config.alarm.pressureMax);
	}
	
	@Subscribe
	public void onAnalogData(final AnalogDataEvent evt) {
		handleHopperAlarm(evt.getMainHopper());
		handlePressureAlarm(evt.getPressure());
	}

	@Subscribe
	public void onCoilReset(CoilResetEvent event) {
		resetDia();
	}
	
	@Subscribe
	public void onProductionModeChange(final ProductionModeChangeEvent evt) {
		if (evt.getMode()==Mode.SETUP) {
			chbHopperSilence.setSelection(true);
			chbHopperSilence.setText("Silence");
			hopperSilenceCount=-1;
			
			chbDiaSilence.setSelection(true);
			chbDiaSilence.setText("Silence");
			diaSilenceCount=-1;
		} else if (evt.getMode()==Mode.EXTRUDE) {
			chbHopperSilence.setSelection(false);
			chbHopperSilence.setText("Silence");
			hopperSilenceCount=0;
			
			chbDiaSilence.setSelection(false);
			chbDiaSilence.setText("Silence");
			diaSilenceCount=0;
			resetDia();
			
			chbPresSilence.setSelection(false);
			chbPresSilence.setText("Silence");
			pressureSilenceCount=0;
		} else if (evt.getMode()==Mode.COMPOUND) {
			chbHopperSilence.setSelection(false);
			chbHopperSilence.setText("Silence");
			hopperSilenceCount=0;
			
			chbDiaSilence.setSelection(true);
			chbDiaSilence.setText("Silence");
			diaSilenceCount=-1;
		}
	}
	
	@Subscribe
	public void onInidcatorData(final IndicatorDataEvent evt) {
	
		if (diaSilenceCount>0) {
			diaSilenceCount--;
			if (chbDiaSilence.getSelection()) {
				if (diaSilenceCount==0) {
					chbDiaSilence.setText("Silence");
					chbDiaSilence.setSelection(false);
				} else {
					chbDiaSilence.setText("Silence:  "+(diaSilenceCount/usbEventHz+1));	
				}
			}
		} else if (diaSilenceCount==0) {
			if (evt.getMax()>curDiaMax) {
				curDiaMax=evt.getMax();
				lblMax.setText(String.format("%.3f", evt.getMax()));
			}
			if (curDiaMin<0.1||evt.getMin()<curDiaMin) {
				curDiaMin=evt.getMin();
				lblMin.setText(String.format("%.3f", evt.getMin()));
			}
			
			if (evt.getMax()>config.alarm.diaUpperThreshold) {
				diaResetCount=config.alarm.diaAlarmResetSeconds*usbEventHz;
				++diaOverCount;
				lblOver.setText(""+diaOverCount);
			} else if (evt.getMin()<config.alarm.diaLowerThreshold) {
				diaResetCount=config.alarm.diaAlarmResetSeconds*usbEventHz;
				++diaUnderCount;
				lblUnder.setText(""+diaUnderCount);
			}
			
			if (diaRepeatCount>0) {
				diaRepeatCount--;
			} else {
				if (diaOverCount>=config.alarm.diaOverCountTrigger) {
					diaRepeatCount=config.alarm.diaAlarmRepeatSeconds*usbEventHz;
					soundDiaOverAlarm();
				}
				if (diaUnderCount>=config.alarm.diaUnderCountTrigger) {
					diaRepeatCount=config.alarm.diaAlarmRepeatSeconds*usbEventHz;
					soundDiaUnderAlarm();
				}
			}
		}
		
		
		if (diaResetCount>0) {
			diaResetCount--;
		} else if (diaResetCount==0) {
			if (diaOverCount<config.alarm.diaOverCountTrigger) {
				needsReset=false;
				diaOverCount=0;
				lblOver.setText(""+diaOverCount);
				diaResetCount=-1;
			}
			if (diaUnderCount<config.alarm.diaUnderCountTrigger) {
				needsReset=false;
				diaUnderCount=0;
				lblUnder.setText(""+diaUnderCount);
				diaResetCount=-1;
			}
		}
	}
	
	private void soundDiaUnderAlarm() {
		if (needsReset) am.playClip("dia-reset");
		else am.playClip("undersize");
		needsReset=true;
	}

	private void soundDiaOverAlarm() {
		if (needsReset) am.playClip("dia-reset");
		else am.playClip("oversize");
		needsReset=true;
	}

	protected void handlePressureAlarm(int curPressure) {
//		if (curPressure > config.alarm.pressureMax) log.info("");
		if (pressureSilenceCount>0) {
			--pressureSilenceCount;
			if (chbPresSilence.getSelection()) {
				if (pressureSilenceCount==0) {
					chbPresSilence.setText("Silence");
					chbPresSilence.setSelection(false);
				} else {
					chbPresSilence.setText("Silence:  "+(pressureSilenceCount/usbEventHz+1));
				}
			}
		} else {
			if (pressureAlarmCount>0) {
				--pressureAlarmCount;
			} else {
				if (curPressure > config.alarm.pressureMax) {
					log.info("");
					soundOverPressureAlarm();
					pressureAlarmCount=config.alarm.pressureAlarmRepeatSeconds*usbEventHz;
				}
			}
		}
		
	}
	
	protected void handleHopperAlarm(int hopperData) {

		if (chbMonitorFeeders.getSelection()) {
			lblHopperData.setText("feeders");    //  FIXME
			if (!fm1.isConnected()) ++hopperDisconnectCount;
			else if (fm1.isEmpty()) ++hopperEmptyCount;
			else ++hopperFullCount;
		} else {
			lblHopperData.setText(""+hopperData);
			if (hopperData<config.alarm.hopperDisconnectThreshold) ++hopperDisconnectCount;
			else if (hopperData>config.alarm.hopperEmptyThreshold) ++hopperEmptyCount;
			else ++hopperFullCount;
		}
	
		log.info(hopperDisconnectCount+":"+hopperEmptyCount+":"+hopperFullCount+"#"+config.alarm.hopperDisconnectThreshold+"#"+config.alarm.hopperEmptyThreshold);

		
		if (hopperSilenceCount>0) {
			--hopperSilenceCount;
			if (chbHopperSilence.getSelection()) {
				if (hopperSilenceCount==0) {
					chbHopperSilence.setText("Silence");
					chbHopperSilence.setSelection(false);
				} else {
					chbHopperSilence.setText("Silence:  "+(hopperSilenceCount/usbEventHz+1));
				}
			}
		} else if (hopperSilenceCount==0) {
			if (hopperDisconnectCount+hopperEmptyCount+hopperFullCount>config.alarm.hopperRepeatSeconds*usbEventHz) {
				if (hopperDisconnectCount>hopperFullCount) soundDisconnectedAlarm();
				else if (hopperEmptyCount>hopperFullCount) soundEmptyAlarm();
				hopperDisconnectCount=0;
				hopperEmptyCount=0;
				hopperFullCount=0;
			}
		}
	}
	
	protected void resetDia() {
		lblPrevMin.setText(lblMin.getText());
		lblPrevMax.setText(lblMax.getText());
		curDiaMin=0;
		curDiaMax=0;
		lblMin.setText(String.format("%.3f", curDiaMin));
		lblMax.setText(String.format("%.3f", curDiaMax));
		diaOverCount=0;
		diaUnderCount=0;
		diaRepeatCount=0;
		lblOver.setText(""+diaOverCount);
		lblUnder.setText(""+diaUnderCount);
		needsReset=false;
		if (!chbDiaSilence.getSelection()) diaSilenceCount=0;
	}

	public void soundDisconnectedAlarm() {
		am.playClip("hopper-discon");
	}
	
	public void soundEmptyAlarm() {
		am.playClip("hopper-empty");
	}
	
	private void soundOverPressureAlarm() {
		am.playClip("pressure-high");
	}
	
	
	
	public void test_mass() {
		eb.post(new StepperSpeedChangeEvent(StepperFunction.TopRoller, 4000));
		eb.post(new StepperRunEvent(StepperFunction.TopRoller));
	}
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
