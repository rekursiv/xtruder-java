package com.protoplant.xtruder2.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperConfigManager;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.StepperConnectEvent;
import com.protoplant.xtruder2.event.StepperDisconnectEvent;
import com.protoplant.xtruder2.event.StepperResetEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;
import com.protoplant.xtruder2.event.StepperStopEvent;


public class StepperPanel extends Group {

	protected Logger log;
	protected EventBus eb;
	protected StepperFunction function;

	protected Label lblTorque;
	protected Label lblSetpt;

	protected Label lblSpeed;
	protected Label lblStatus;
	protected StepperConfigManager scm;
	private boolean isReversed = false;
	protected boolean isRunning = false;
	protected Button btnReset;
	protected int speed;


	public StepperPanel(Composite parent, Injector injector, StepperFunction function) {
		super(parent, SWT.NONE);
		this.function = function;
		setLayout(null);

		lblSetpt = new Label(this, SWT.NONE);
		lblSetpt.setBounds(25, 22, 110, 15);
		lblSetpt.setText("Setpoint:");
		
		lblTorque = new Label(this, SWT.NONE);
		lblTorque.setBounds(257, 22, 110, 15);

		lblSpeed = new Label(this, SWT.NONE);
		lblSpeed.setBounds(141, 22, 110, 15);
		
		lblStatus = new Label(this, SWT.NONE);
		lblStatus.setBounds(373, 22, 134, 15);
		
		btnReset = new Button(this, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				reset();
			}
		});
		btnReset.setBounds(540, 12, 46, 25);
		btnReset.setText("Reset");

		showDisconnectState();
		
		if (injector!=null) injector.injectMembers(this);
	}
	


	@Inject
	public void inject(Logger log, EventBus eb, StepperConfigManager scm) {
		this.log = log;
		this.eb = eb;
		this.scm = scm;
		setText(function.name());
	}

	@Subscribe
	public void onConnect(StepperConnectEvent evt) {
		if (evt.getFunction()==function) {
			adjustSpeed();
			if (isRunning) eb.post(new StepperRunEvent(function));
		}
	}
	
	@Subscribe
	public void onDisconnect(StepperDisconnectEvent evt) {
		if (evt.getFunction() == function) {
			showDisconnectState();
		}
	}
	
	private void showDisconnectState() {
		lblTorque.setText("Torque: ~");
		lblSpeed.setText("Speed: ~");
		lblStatus.setText("Status: DISCONNECTED");
	}
	
	@Subscribe
	public void onStatus(final StepperStatusEvent evt) {
		if (evt.getFunction()==function) {
			lblTorque.setText("Torque: "+evt.getTorque());
			lblSpeed.setText("Speed: "+evt.getSpeed());
			lblStatus.setText("Status: "+toBinary(evt.getStatus()));
		}
	}
	
	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		isReversed = scm.getConfig(function).isReversed;
	}
	
	public void setSpeed(int speed) {  //  FIXME:  set slider in Adjustable subclass
		this.speed = speed;
		adjustSpeed();
	}

	protected void adjustSpeed() {
		int dirSpeed = speed;
		if (isReversed) dirSpeed = -speed;
		lblSetpt.setText("Setpoint: "+dirSpeed);
		eb.post(new StepperSpeedChangeEvent(function, dirSpeed));
//		log.info(""+dirSpeed);
	}
	
	public void run() {
		eb.post(new StepperRunEvent(function));
		isRunning = true;
	}
	
	public void stop() {
		eb.post(new StepperStopEvent(function));
		isRunning = false;
	}
	
	protected void reset() {
		stop();
		eb.post(new StepperResetEvent(function));
	}
	
	protected String toBinary(int byteData) {
		String bits = "0000000"+Integer.toBinaryString(byteData)+" ";
		return bits.substring(bits.length() - 9, bits.length());
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
