package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.SWTResourceManager;
import com.protoplant.xtruder2.config.StepperConfigManager;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.UsbStatusEvent;

public class StatusSummaryPanel extends BaseSummaryPanel {

	protected Label lblUsbCons;
	protected Label lblUsbConsTitle;
	private StepperConfigManager scm;
	
	private int curNumDevs = 0;
	
	public StatusSummaryPanel(Composite parent, Injector injector) {
		super(parent, injector);
		setText("System Status");
		
		lblUsbCons = new Label(this, SWT.NONE);
		lblUsbCons.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});

		lblUsbCons.setBackground(null);
		lblUsbCons.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.NORMAL));
		lblUsbCons.setBounds(10, 24, 116, 32);
		
		lblUsbConsTitle = new Label(this, SWT.NONE);
		lblUsbConsTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});
		lblUsbConsTitle.setBounds(20, 62, 128, 20);
		lblUsbConsTitle.setText("USB Connections");
		
	}
	
	@Inject
	public void inject(Logger log, EventBus eb, StepperConfigManager scm) {
		this.log = log;
		this.eb = eb;
		this.scm = scm;
		
	}
	
	@Subscribe
	public void onUsbStatusEvent(UsbStatusEvent evt) {
		curNumDevs = evt.getNumDevs();
		updateDisplay();
	}
	
	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		updateDisplay();		
	}
	
	public void updateDisplay() {
		lblUsbCons.setText(curNumDevs+"/"+(scm.getNumDefinedSteppers()+2));
	}
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
