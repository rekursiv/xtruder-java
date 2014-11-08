package com.protoplant.xtruder2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

public class StatusPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StatusPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
