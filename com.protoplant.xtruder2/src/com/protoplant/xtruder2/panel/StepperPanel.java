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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;


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
		setLayout(new FormLayout());

		lblSetpt = new Label(this, SWT.NONE);
		FormData fd_lblSetpt = new FormData();
		fd_lblSetpt.bottom = new FormAttachment(0, 27);
		fd_lblSetpt.right = new FormAttachment(0, 132);
		fd_lblSetpt.top = new FormAttachment(0, 7);
		fd_lblSetpt.left = new FormAttachment(0, 22);
		lblSetpt.setLayoutData(fd_lblSetpt);
		lblSetpt.setText("Setpoint:");
		
		lblTorque = new Label(this, SWT.NONE);
		FormData fd_lblTorque = new FormData();
		fd_lblTorque.bottom = new FormAttachment(0, 27);
		fd_lblTorque.right = new FormAttachment(0, 364);
		fd_lblTorque.top = new FormAttachment(0, 7);
		fd_lblTorque.left = new FormAttachment(0, 254);
		lblTorque.setLayoutData(fd_lblTorque);

		lblSpeed = new Label(this, SWT.NONE);
		FormData fd_lblSpeed = new FormData();
		fd_lblSpeed.bottom = new FormAttachment(0, 27);
		fd_lblSpeed.right = new FormAttachment(0, 248);
		fd_lblSpeed.top = new FormAttachment(0, 7);
		fd_lblSpeed.left = new FormAttachment(0, 138);
		lblSpeed.setLayoutData(fd_lblSpeed);
		
		lblStatus = new Label(this, SWT.NONE);
		FormData fd_lblStatus = new FormData();
		fd_lblStatus.bottom = new FormAttachment(0, 27);
		fd_lblStatus.right = new FormAttachment(0, 541);
		fd_lblStatus.top = new FormAttachment(0, 7);
		fd_lblStatus.left = new FormAttachment(0, 370);
		lblStatus.setLayoutData(fd_lblStatus);
		
		btnReset = new Button(this, SWT.NONE);
		FormData fd_btnReset = new FormData();
		fd_btnReset.right = new FormAttachment(0, 631);
		fd_btnReset.top = new FormAttachment(0, 2);
		fd_btnReset.left = new FormAttachment(0, 569);
		btnReset.setLayoutData(fd_btnReset);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				reset();
			}
		});
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
