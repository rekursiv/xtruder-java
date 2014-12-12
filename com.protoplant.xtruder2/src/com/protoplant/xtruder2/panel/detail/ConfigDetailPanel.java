package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.XtruderConfig;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;

import util.config.ConfigManager;

import org.eclipse.swt.widgets.Text;

public class ConfigDetailPanel extends Composite {
	private Logger log;
	private XtruderConfig config;
	private ConfigManager<XtruderConfig> cfgMgr;
	protected Text text;

	public ConfigDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		text = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, XtruderConfig config, ConfigManager<XtruderConfig> cfgMgr) {
		this.log = log;
		this.config = config;
		this.cfgMgr = cfgMgr;
		
		try {
			text.setText(cfgMgr.getText(config));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
