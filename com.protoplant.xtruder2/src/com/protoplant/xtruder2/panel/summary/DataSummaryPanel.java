package com.protoplant.xtruder2.panel.summary;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import com.protoplant.xtruder2.SWTResourceManager;
import com.protoplant.xtruder2.event.AnalogDataEvent;
import com.protoplant.xtruder2.event.IndicatorDataEvent;

public class DataSummaryPanel extends BaseSummaryPanel {

	protected Label lblPsi;
	protected Label lblPsiTitle;
	protected Label lblMm;
	protected Label lblMmTitle;
	
	
	public DataSummaryPanel(Composite parent, Injector injector) {
		super(parent, injector);
		setText("Data");
		
		
		lblPsi = new Label(this, SWT.NONE);
		lblPsi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});

		lblPsi.setBackground(null);
		lblPsi.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.NORMAL));
		lblPsi.setBounds(10, 24, 116, 32);
		lblPsi.setText("0");
		
		
		lblPsiTitle = new Label(this, SWT.NONE);
		lblPsiTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});
		lblPsiTitle.setBounds(20, 62, 128, 20);
		lblPsiTitle.setText("PSI");
		
		
		lblMm = new Label(this, SWT.NONE);
		lblMm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});

		lblMm.setBackground(null);
		lblMm.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.NORMAL));
		lblMm.setBounds(231, 24, 216, 32);
		lblMm.setText("0");
		
		
		lblMmTitle = new Label(this, SWT.NONE);
		lblMmTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});
		lblMmTitle.setBounds(251, 62, 228, 20);
		lblMmTitle.setText("mm");
		
		if (injector!=null) injector.injectMembers(this);
	}

	
	@Subscribe
	public void onInidcatorData(final IndicatorDataEvent evt) {
		lblMm.setText(String.format("%.3f", evt.getCur()));
	}
	
	@Subscribe
	public void onAnalogData(final AnalogDataEvent evt) {
		lblPsi.setText(String.format("%.0f", evt.data1*0.3f));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}