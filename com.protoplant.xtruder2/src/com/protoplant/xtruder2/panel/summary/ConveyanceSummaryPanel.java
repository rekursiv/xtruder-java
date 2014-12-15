package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.SWTResourceManager;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.event.StepperStatusEvent;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class ConveyanceSummaryPanel extends BaseSummaryPanel {
	protected Label lblIps;
	protected Label lblIpsTitle;

	public ConveyanceSummaryPanel(Composite parent, Injector injector) {
		super(parent, null);
		setText("Conveyance");
		
		lblIps = new Label(this, SWT.NONE);
		lblIps.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});

		lblIps.setBackground(null);
		lblIps.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.NORMAL));
		lblIps.setBounds(10, 24, 116, 32);
		lblIps.setText("0");
		
		
		lblIpsTitle = new Label(this, SWT.NONE);
		lblIpsTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});
		lblIpsTitle.setBounds(32, 62, 116, 15);
		lblIpsTitle.setText("Inches Per Second");
		
		if (injector!=null) injector.injectMembers(this);
	}
	
	@Subscribe
	public void onStatus(final StepperStatusEvent evt) {
		if (evt.getFunction()==StepperFunction.TopRoller) {
			int speed = Math.abs(evt.getSpeed());
			int stepsPerRev = 200;  // for 1.8 deg/step motor
			int microStepsPerStep = 16;
			int microStepsPerRev = stepsPerRev*microStepsPerStep;
			float rollerDiameter = 3.0f;
			float rollerCircumference = rollerDiameter*(float)Math.PI;
			float ips = ((float)speed/(float)microStepsPerRev)*rollerCircumference;
			lblIps.setText(String.format("%.2f", ips));
		}
	}
		

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
