package com.protoplant.xtruder2;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class StepperPanel extends Group {

	private Logger log;
	private EventBus eb;

	private int sliderVal = 0;
	private Label lblTorque;
	private Label lblSliderValue;
	private Scale slider;

	private int scaleRange = 65000;
	private Label lblSpeed;
	private Label lblStatus;

	public StepperPanel(Composite parent, Injector injector, StepperConfig config) {
		super(parent, SWT.NONE);
		setText("Stepper "+config.serial);
		
		lblTorque = new Label(this, SWT.NONE);
		lblTorque.setBounds(438, 74, 55, 15);
		lblTorque.setText("Torque");

		lblSliderValue = new Label(this, SWT.NONE);
		lblSliderValue.setBounds(24, 74, 87, 15);
		lblSliderValue.setText("slider val");
		
		slider = new Scale(this, SWT.NONE);
		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				onSpeedChange();
			}
		});
		slider.setMaximum(scaleRange);
		slider.setMinimum(0);
		slider.setBounds(10, 24, 820, 44);
		slider.setSelection(scaleRange/2);
		
		Button btnStop = new Button(this, SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				slider.setSelection(scaleRange/2);
				onSpeedChange();
			}
		});
		btnStop.setBounds(137, 69, 75, 25);
		btnStop.setText("STOP");
		
		lblSpeed = new Label(this, SWT.NONE);
		lblSpeed.setBounds(344, 74, 55, 15);
		lblSpeed.setText("Speed");
		
		lblStatus = new Label(this, SWT.NONE);
		lblStatus.setBounds(572, 74, 155, 15);
		lblStatus.setText("Status");

		if (injector!=null) injector.injectMembers(this);
	}
	
	
	public void onSpeedChange() {
		sliderVal = slider.getSelection()-scaleRange/2;
		eb.post(new StepperSpeedChangeEvent(sliderVal));
		lblSliderValue.setText(""+sliderVal);
//		lblTorque.setText(calcTorque());
	}
	
	public int calcTorque() {
//		return (sliderVal/120)+1;
		int curMotorSpeed = sliderVal;
		int holdingTorque = 1;
		int minTorque = 10;
		int maxTorque = 100;
		int torqueDiv = 255;
		
		int torque = holdingTorque;
		if (curMotorSpeed>0) {
			torque = (curMotorSpeed/torqueDiv)+minTorque;
			if (torque>maxTorque) torque=maxTorque;
		}
		return torque;
	}
	
	
	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
	}

	@Subscribe
	public void onData(final StepperStatusEvent evt) {
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
				lblTorque.setText(""+evt.getTorque());
				lblSpeed.setText(""+evt.getSpeed());
				lblStatus.setText(toBinary(evt.getStatus()));
//			}
//		});
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
