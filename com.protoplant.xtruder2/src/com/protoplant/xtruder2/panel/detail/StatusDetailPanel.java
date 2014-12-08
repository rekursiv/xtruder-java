package com.protoplant.xtruder2.panel.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;

public class StatusDetailPanel extends Composite {
	protected Group grpStatusDetail;

	public StatusDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		grpStatusDetail = new Group(this, SWT.NONE);
		grpStatusDetail.setText("Status Detail");
//		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
