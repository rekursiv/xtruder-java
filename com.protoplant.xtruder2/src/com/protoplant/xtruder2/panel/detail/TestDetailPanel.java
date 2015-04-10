package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.config.XtruderConfig;

public class TestDetailPanel extends Composite {

	private Logger log;
	private EventBus eb;
	private XtruderConfig config;
	protected Label lblTest;

	
	public TestDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		
		lblTest = new Label(this, SWT.NONE);
		lblTest.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblTest.setBounds(20, 56, 569, 45);
		
		Button btnTest = new Button(this, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				test();
			}
		});
		btnTest.setBounds(20, 25, 75, 25);
		btnTest.setText("TEST");
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config) {
		this.log = log;
		this.eb = eb;
		this.config = config;
	}
	
	public void test() {
//		lblTest.setText("TEST");
		log.info("----------------------------------");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
