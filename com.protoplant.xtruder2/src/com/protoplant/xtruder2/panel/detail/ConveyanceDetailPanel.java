package com.protoplant.xtruder2.panel.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;

public class ConveyanceDetailPanel extends Composite {
	protected Group grpConveyanceDetail;

	public ConveyanceDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		grpConveyanceDetail = new Group(this, SWT.NONE);
		grpConveyanceDetail.setText("Conveyance Detail");
//		if (injector!=null) injector.injectMembers(this);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
