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
import com.protoplant.xtruder2.AudioManager;
import com.protoplant.xtruder2.config.XtruderConfig;

public class TestDetailPanel extends Composite {

	private Logger log;
	private EventBus eb;
	private XtruderConfig config;
	protected Label lblTest;
	private AudioManager am;

	
	public TestDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		
		lblTest = new Label(this, SWT.NONE);
		lblTest.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblTest.setBounds(10, 199, 569, 45);
		
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
	public void inject(Logger log, EventBus eb, XtruderConfig config, AudioManager am) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.am = am;
	}

	public void test() {
		testVoices();
		//System.getProperty("os.name" );
//		am.playClip("50gtg");
	}
	
	public void testVoices() {
		lblTest.setText("Testing voices...");
		am.playClip("50gtg");
		delay(2000);
		am.playClip("30gtg");
		delay(2000);
		am.playClip("10gtg");
		delay(2000);
		am.playClip("5");
		delay(1000);
		am.playClip("4");
		delay(1000);
		am.playClip("3");
		delay(1000);
		am.playClip("2");
		delay(1000);
		am.playClip("1");
		delay(1000);
		am.playClip("mark");
		delay(2000);
		am.playClip("dia-reset");
		delay(2000);
		am.playClip("undersize");
		delay(1000);
		am.playClip("oversize");
		delay(1000);
		am.playClip("hopper-discon");
		delay(2000);
		am.playClip("hopper-empty");
		delay(2000);
		am.playClip("pressure-high");
		lblTest.setText("Done.");
	}

	private void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
