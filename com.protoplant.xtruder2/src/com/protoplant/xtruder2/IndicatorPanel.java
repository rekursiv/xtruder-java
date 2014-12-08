package com.protoplant.xtruder2;

import java.util.logging.Logger;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.IndicatorZeroEvent;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class IndicatorPanel extends Group {
	private Label lblMin;
	private Label lblDia;
	private Label lblMax;
	private Logger log;
	private EventBus eb;
	private Button btnZero;

	float cur;
	float min;
	float max;
	private Button btnReset;

	public IndicatorPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		setText("Indicator");
		
		lblMin = new Label(this, SWT.NONE);
		lblMin.setBounds(10, 23, 100, 24);
		lblMin.setText("MIN");
		
		lblDia = new Label(this, SWT.NONE);
		lblDia.setBounds(266, 23, 106, 25);
		lblDia.setText("CUR");
		
		lblMax = new Label(this, SWT.NONE);
		lblMax.setBounds(129, 23, 106, 24);
		lblMax.setText("MAX");
		
		btnZero = new Button(this, SWT.NONE);
		btnZero.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eb.post(new IndicatorZeroEvent());
			}
		});
		btnZero.setBounds(10, 53, 75, 25);
		btnZero.setText("Zero");
		
		btnReset = new Button(this, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				reset();
			}
		});
		btnReset.setBounds(91, 53, 75, 25);
		btnReset.setText("Reset");

		if (injector!=null) injector.injectMembers(this);

	}
	
	
	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
		reset();
	}

	
	@Subscribe
	public void onData(final IndicatorDataEvent evt) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				cur=evt.getCur();
				lblDia.setText(String.format("%.3f", cur));
				if (evt.getMin()<min) {
					min=evt.getMin();
					lblMin.setText(String.format("%.3f", min));
				} else if (evt.getMax()>max) {
					max=evt.getMax();
					lblMax.setText(String.format("%.3f", max));
				}
			}
		});
	}
	
	public void reset() {
		min = cur;
		lblMin.setText(String.format("%.2f", min));
		max = cur;
		lblMax.setText(String.format("%.2f", max));
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
