package com.protoplant.xtruder2.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperType;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class StepperPanel extends Group {

	protected Logger log;
	protected EventBus eb;
	protected StepperType type;

	private Label lblTorque;
	private Label lblSetpt;

	private Label lblSpeed;
	private Label lblStatus;

	public StepperPanel(Composite parent, Injector injector, StepperType type) {
		super(parent, SWT.NONE);
		this.type = type;
		setLayout(null);
		
		lblTorque = new Label(this, SWT.NONE);
		lblTorque.setBounds(257, 22, 110, 15);
		lblTorque.setText("Torque:");

		lblSetpt = new Label(this, SWT.NONE);
		lblSetpt.setBounds(25, 22, 110, 15);
		lblSetpt.setText("Setpoint:");
		
		lblSpeed = new Label(this, SWT.NONE);
		lblSpeed.setBounds(141, 22, 110, 15);
		lblSpeed.setText("Speed:");
		
		lblStatus = new Label(this, SWT.NONE);
		lblStatus.setBounds(373, 22, 167, 15);
		lblStatus.setText("Status:");

		if (injector!=null) injector.injectMembers(this);
	}
	

	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
		setText(type.name());
	}

	@Subscribe
	public void onSpeedChange(StepperSpeedChangeEvent evt) {
		if (evt.getType() == type) {
			lblSetpt.setText("Setpoint: "+evt.getSpeed());
		}
	}
	
	@Subscribe
	public void onData(final StepperStatusEvent evt) {
//		if (config.steppers.length<index) return;
		if (evt.getType()==type) {
			lblTorque.setText("Torque: "+evt.getTorque());
			lblSpeed.setText("Speed: "+evt.getSpeed());
			lblStatus.setText("Status: "+toBinary(evt.getStatus()));
		}
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
