package com.protoplant.xtruder2.panel.detail;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;


public class DataDetailPanel extends Composite {


	public DataDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		new ChartPanel(this, injector);
		
//		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
