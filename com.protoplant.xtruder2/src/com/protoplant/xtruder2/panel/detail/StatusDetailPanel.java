package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Level;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;

import util.logging.LogSetup;
import util.logging.LogView;

public class StatusDetailPanel extends Composite {
	
	protected LogView pnlLogView;

	public StatusDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		pnlLogView = new LogView(this, SWT.NONE);
		
		LogSetup.initView(pnlLogView, Level.ALL);

//		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
