package com.protoplant.xtruder2.test;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.AnalogDataEvent;

import org.eclipse.swt.widgets.Label;

public class AnalogPanel extends Group {

	private Logger log;
	private EventBus eb;
	private Label lblData1;
	private Label lblData2;
	private Label lblData3;
	private Label lblData4;
	private Label lblTest;


	public AnalogPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		setText("Analog");
		
		lblData1 = new Label(this, SWT.NONE);
		lblData1.setBounds(10, 27, 106, 33);
		lblData1.setText("Data 1");
		
		lblData2 = new Label(this, SWT.NONE);
		lblData2.setText("Data 2");
		lblData2.setBounds(10, 66, 106, 33);
		
		lblData3 = new Label(this, SWT.NONE);
		lblData3.setText("Data 3");
		lblData3.setBounds(72, 27, 106, 33);
		
		lblData4 = new Label(this, SWT.NONE);
		lblData4.setText("Data 4");
		lblData4.setBounds(72, 66, 106, 33);
		
		lblTest = new Label(this, SWT.NONE);
		lblTest.setBounds(203, 27, 115, 33);
		lblTest.setText("Test");
		if (injector!=null) injector.injectMembers(this);
	}
	
	
	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
	}

	@Subscribe
	public void onData(final AnalogDataEvent evt) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
/*				
				lblData1.setText(""+evt.data1);
				lblData2.setText(""+evt.data2);
				lblData3.setText(""+evt.data3);
				lblData4.setText(""+evt.data4);
				lblTest.setText(String.format("%.2f", evt.data1*3.363288f)+" psi");
				*/
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
