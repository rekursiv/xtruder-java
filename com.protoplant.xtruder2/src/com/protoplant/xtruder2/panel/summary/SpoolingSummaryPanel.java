package com.protoplant.xtruder2.panel.summary;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.CoilMassEvent;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.SpoolTargetMassEvent;

public class SpoolingSummaryPanel extends BaseSummaryPanel {

	protected Label lblMass;
	protected Label lblMassTitle;
	
	public SpoolingSummaryPanel(Composite parent, Injector injector) {
		super(parent, injector);
		setText("Spooling");
		
		
		lblMass = new Label(this, SWT.NONE);
		lblMass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});

		lblMass.setBackground(null);
		lblMass.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.NORMAL));
		lblMass.setBounds(10, 10, 116, 32);
		lblMass.setText("0.00");
		
		
		lblMassTitle = new Label(this, SWT.NONE);
		lblMassTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});
		lblMassTitle.setBounds(20, 48, 128, 20);
		lblMassTitle.setText("Spool Size");
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Subscribe
	public void onSpoolTargetMass(SpoolTargetMassEvent evt) {
		lblMass.setText(evt.getMass()+"g");
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
