package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.ProductionModeChangeEvent;

import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Label;

public class ProductionPanel extends Group {

	
	public enum Mode {SETUP, COMPOUND, EXTRUDE};
	
	private Logger log;
	private EventBus eb;

	private AlarmPanel pnlAlarm;
	private FeedbackPanel pnlFeedback;
	private Mode curMode = Mode.SETUP;
	
	
	public ProductionPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		setText("Production");
		
		pnlAlarm = new AlarmPanel(this, injector);
		pnlAlarm.setBounds(10, 45, 360, 280);
		
		Button btnSetup = new Button(this, SWT.RADIO);
		btnSetup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (btnSetup.getSelection()) {
					curMode=Mode.SETUP;
					eb.post(new ProductionModeChangeEvent(curMode));
				}
			}
		});
		btnSetup.setSelection(true);
		btnSetup.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnSetup.setBounds(10, 10, 80, 29);
		btnSetup.setText("Setup");
		
		Button btnCompound = new Button(this, SWT.RADIO);
		btnCompound.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (btnCompound.getSelection()) {
					curMode=Mode.COMPOUND;
					eb.post(new ProductionModeChangeEvent(curMode));
				}
			}
		});
		btnCompound.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnCompound.setBounds(238, 10, 112, 29);
		btnCompound.setText("Compound");
		
		Button btnExtrude = new Button(this, SWT.RADIO);
		btnExtrude.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (btnExtrude.getSelection()) {
					curMode=Mode.EXTRUDE;
					eb.post(new ProductionModeChangeEvent(curMode));
				}
			}
		});
		btnExtrude.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnExtrude.setBounds(111, 10, 112, 29);
		btnExtrude.setText("Extrude");
		
		pnlFeedback = new FeedbackPanel(this, injector);
		pnlFeedback.setBounds(10, 331, 360, 134);
		
		Label lblTargetDiameter = new Label(pnlFeedback, SWT.NONE);
		lblTargetDiameter.setBounds(10, 68, 120, 32);
		lblTargetDiameter.setText("Target Diameter:");
		
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
		
		curMode=Mode.SETUP;
		eb.post(new ProductionModeChangeEvent(curMode));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
