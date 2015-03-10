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
import com.protoplant.xtruder2.event.StepperDisconnectEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.panel.AdjustableStepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class TestDetailPanel extends Composite {

	private static final int fullThreshold = 1800;
	private static final int disconnectThreshold = 4000;
	
	private static final int repeatTime = 200;  // 20 seconds
	
	private int fullCount;
	private int emptyCount;
	private int disconnectCount;

	
	
	
	
	
	
	private Logger log;
	private EventBus eb;
	private XtruderConfig config;
	
	protected Button btnTest;
	protected AdjustableStepperPanel asp;
	protected TrackingStepperPanel tsp;
	protected Composite composite;
	protected Label lblTest;
	private Text txtTheHopperIs;
	private AudioManager am;
	
	


	public TestDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		
		
		btnTest = new Button(this, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				test();
			}
		});
		btnTest.setBounds(20, 229, 75, 25);
		btnTest.setText("TEST");
		
		lblTest = new Label(this, SWT.NONE);
		lblTest.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblTest.setBounds(20, 108, 569, 45);
		
		txtTheHopperIs = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
//		txtTheHopperIs.setText("50 grams to go");
		txtTheHopperIs.setBounds(10, 10, 616, 56);
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, AudioManager am) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.am = am;
	}

	
	@Subscribe
	public void onAnalogData(final AnalogDataEvent evt) {

		int curValue = evt.getMainHopper();
		lblTest.setText(""+curValue);		
		if (curValue>disconnectThreshold) ++disconnectCount;
		else if (curValue>fullThreshold) ++fullCount;
		else ++emptyCount;
		
		if (disconnectCount+emptyCount+fullCount>repeatTime) {
			if (disconnectCount>emptyCount) soundDisconnectedAlarm();
			else if (emptyCount>fullCount) soundEmptyAlarm();
			disconnectCount=0;
			emptyCount=0;
			fullCount=0;
		}
		
	}

	public void soundDisconnectedAlarm() {
		log.info("the hopper sensor is disconnected");
		am.speak("the hopper sensor is disconnected");
	}
	
	public void soundEmptyAlarm() {
		log.info("the hopper is almost empty");
		am.speak("the hopper is almost empty");
	}
	
	
	
	
	
	public void test() {
		
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
