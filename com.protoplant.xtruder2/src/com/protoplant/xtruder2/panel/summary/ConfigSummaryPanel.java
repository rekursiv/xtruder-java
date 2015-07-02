package com.protoplant.xtruder2.panel.summary;

import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.ConfigFileSelectEvent;

import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class ConfigSummaryPanel extends BaseSummaryPanel {
	
	private Label lblConfigName;

	public ConfigSummaryPanel(Composite parent, Injector injector) {
		super(parent, injector);
		setText("Config");
		
		lblConfigName = new Label(this, SWT.NONE);
		lblConfigName.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});
		lblConfigName.setBounds(10, 27, 210, 42);
		lblConfigName.setText("ConfigName");
		lblConfigName.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		if (injector!=null) injector.injectMembers(this);
	}

	@Subscribe
	public void onStatus(final ConfigFileSelectEvent evt) {
		lblConfigName.setText(evt.getName());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
