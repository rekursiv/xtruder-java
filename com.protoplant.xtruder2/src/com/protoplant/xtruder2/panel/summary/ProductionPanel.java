package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ProductionPanel extends Group {

	private Logger log;
	private EventBus eb;

	public ProductionPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setText("Production");
		
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
	}

}
