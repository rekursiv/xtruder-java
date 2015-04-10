package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.AudioManager;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.AnalogDataEvent;
import com.protoplant.xtruder2.event.CoilResetEvent;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.panel.AdjustableStepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;
import com.protoplant.xtruder2.usb.UsbManager;
import com.protoplant.xtruder2.usb.UsbModule;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class AlarmDetailPanel extends Composite {

	private int hopperFullCount;
	private int hopperEmptyCount;
	private int hopperDisconnectCount;
	private int hopperSilenceCount;
	private int diaAlarmCount;
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
	

	public AlarmDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		
		Group grpDiameterMinmax = new Group(this, SWT.NONE);
		grpDiameterMinmax.setText("Diameter Min/Max");
		grpDiameterMinmax.setBounds(10,  10, 275, 203);
		
		lblPrevMax = new Label(grpDiameterMinmax, SWT.BORDER);
		lblPrevMax.setBounds(10, 20, 100, 25);
		
		lblPrevMin = new Label(grpDiameterMinmax, SWT.BORDER);
		lblPrevMin.setBounds(10, 51, 100, 25);
		
		lblMax = new Label(grpDiameterMinmax, SWT.BORDER);
		lblMax.setBounds(130, 20, 100, 25);
		
		lblMin = new Label(grpDiameterMinmax, SWT.BORDER);
		lblMin.setBounds(130, 51, 100, 25);
		
		btnResetDia = new Button(grpDiameterMinmax, SWT.NONE);
		btnResetDia.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				resetDia();
			}
		});
		btnResetDia.setBounds(10, 154, 100, 35);
		btnResetDia.setText("Reset");
		
		Label lblOverTitle = new Label(grpDiameterMinmax, SWT.NONE);
		lblOverTitle.setBounds(10, 92, 100, 25);
		lblOverTitle.setText("# Over:");
		
		Label lblUnderTitle = new Label(grpDiameterMinmax, SWT.NONE);
		lblUnderTitle.setText("# Under:");
		lblUnderTitle.setBounds(10, 123, 100, 25);
		
		lblOver = new Label(grpDiameterMinmax, SWT.BORDER);
		lblOver.setBounds(130, 92, 100, 25);
		
		lblUnder = new Label(grpDiameterMinmax, SWT.BORDER);
		lblUnder.setBounds(130, 123, 100, 25);
		
		chbDiaSilence = new Button(grpDiameterMinmax, SWT.CHECK);
		chbDiaSilence.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (chbDiaSilence.getSelection()) {
					diaAlarmCount=config.alarm.diaAlarmSilenceSeconds*usbEventHz;
				} else {
					chbDiaSilence.setText("Silence");
					diaAlarmCount=0;
				}
			}
		});
		chbDiaSilence.setBounds(123, 156, 129, 30);
		chbDiaSilence.setText("Silence");
		
		Group grpHopper = new Group(this, SWT.NONE);
		grpHopper.setText("Hopper");
		grpHopper.setBounds(291, 10, 148, 118);
		
		lblHopperData = new Label(grpHopper, SWT.BORDER);
		lblHopperData.setBounds(10, 26, 128, 30);
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
		chbHopperSilence.setBounds(10, 72, 128, 36);
		
		Button btnTest = new Button(this, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				test();
			}
		});
		btnTest.setBounds(315, 152, 75, 25);
		btnTest.setText("TEST");
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, AudioManager am) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.am = am;
		diaOverCount=0;
		diaUnderCount=0;
		diaResetCount=-1;
		needsReset=false;
		lblOver.setText(""+diaOverCount);
		lblUnder.setText(""+diaUnderCount);
		usbEventHz=1000/UsbManager.IO_REFRESH_PERIOD;
	}

	
	@Subscribe
	public void onInidcatorData(final IndicatorDataEvent evt) {
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

		
		if (diaAlarmCount>0) {
			diaAlarmCount--;
			if (chbDiaSilence.getSelection()) {
				if (diaAlarmCount==0) {
					chbDiaSilence.setText("Silence");
					chbDiaSilence.setSelection(false);
				} else {
					chbDiaSilence.setText("Silence:  "+(diaAlarmCount/usbEventHz+1));	
				}
			}
		} else {
			if (diaOverCount>=config.alarm.diaOverCountTrigger) {
				diaAlarmCount=config.alarm.diaAlarmRepeatSeconds*usbEventHz;
				soundDiaOverAlarm();
			}
			if (diaUnderCount>=config.alarm.diaUnderCountTrigger) {
				diaAlarmCount=config.alarm.diaAlarmRepeatSeconds*usbEventHz;
				soundDiaUnderAlarm();
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
		if (needsReset) am.speak("diameter needs reset");
		else am.speak("undersize");
		needsReset=true;
	}

	private void soundDiaOverAlarm() {
		if (needsReset) am.speak("diameter needs reset");
		else am.speak("oversize");
		needsReset=true;
	}

	@Subscribe
	public void onAnalogData(final AnalogDataEvent evt) {
		int curValue = evt.getMainHopper();
		lblHopperData.setText(""+curValue);		
		if (curValue>config.alarm.hopperDisconnectThreshold) ++hopperDisconnectCount;
		else if (curValue>config.alarm.hopperFullThreshold) ++hopperFullCount;
		else ++hopperEmptyCount;
		
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
		} else {
			if (hopperDisconnectCount+hopperEmptyCount+hopperFullCount>config.alarm.hopperRepeatSeconds*usbEventHz) {
				if (hopperDisconnectCount>hopperEmptyCount) soundDisconnectedAlarm();
				else if (hopperEmptyCount>hopperFullCount) soundEmptyAlarm();
				hopperDisconnectCount=0;
				hopperEmptyCount=0;
				hopperFullCount=0;
			}
		}
		
	}
	
	@Subscribe
	public void onCoilReset(CoilResetEvent event) {
		resetDia();
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
		lblOver.setText(""+diaOverCount);
		lblUnder.setText(""+diaUnderCount);
		needsReset=false;
		diaAlarmCount=0;
	}

	public void soundDisconnectedAlarm() {
		am.speak("the hopper sensor is disconnected");
	}
	
	public void soundEmptyAlarm() {
		am.speak("the hopper is almost empty");
	}
	
	
	
	
	
	public void test_mass() {
		eb.post(new StepperSpeedChangeEvent(StepperFunction.TopRoller, 4000));
		eb.post(new StepperRunEvent(StepperFunction.TopRoller));
	}
	
	public void test() {
		soundEmptyAlarm();
		soundDisconnectedAlarm();
		soundDiaUnderAlarm();
		soundDiaOverAlarm();
		
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
