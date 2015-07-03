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
		pnlAlarm.setBounds(10, 93, 302, 280);
		
		Button btnSetup = new Button(this, SWT.RADIO);
		btnSetup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				curMode=Mode.SETUP;
				eb.post(new ProductionModeChangeEvent(curMode));   ///////////////////////////////////////////  FIXME
			}
		});
		btnSetup.setSelection(true);
		btnSetup.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnSetup.setBounds(10, 23, 80, 29);
		btnSetup.setText("Setup");
		
		Button btnCompound = new Button(this, SWT.RADIO);
		btnCompound.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				curMode=Mode.COMPOUND;
				eb.post(new ProductionModeChangeEvent(curMode));
			}
		});
		btnCompound.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnCompound.setBounds(214, 23, 112, 29);
		btnCompound.setText("Compound");
		
		Button btnExtrude = new Button(this, SWT.RADIO);
		btnExtrude.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				curMode=Mode.EXTRUDE;
				eb.post(new ProductionModeChangeEvent(curMode));
			}
		});
		btnExtrude.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnExtrude.setBounds(96, 23, 112, 29);
		btnExtrude.setText("Extrude");
		
		Button btnResetCoil = new Button(this, SWT.NONE);
		btnResetCoil.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnResetCoil.setBounds(10, 58, 302, 29);
		btnResetCoil.setText("Reset Coil");
		
		pnlFeedback = new FeedbackPanel(this, injector);
		pnlFeedback.setBounds(10, 390, 348, 71);
		
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
