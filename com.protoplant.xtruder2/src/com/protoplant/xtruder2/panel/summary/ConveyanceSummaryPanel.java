package com.protoplant.xtruder2.panel.summary;

import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;
import com.protoplant.xtruder2.SWTResourceManager;

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
		super(parent, injector);
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
		lblIps.setText("11.367");
		
		
		lblIpsTitle = new Label(this, SWT.NONE);
		lblIpsTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});
		lblIpsTitle.setBounds(32, 62, 116, 15);
		lblIpsTitle.setText("Inches Per Second");
//		if (injector!=null) injector.injectMembers(this);
	}
	
	


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
