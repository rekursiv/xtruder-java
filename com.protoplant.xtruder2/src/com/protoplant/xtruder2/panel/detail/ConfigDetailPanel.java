package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperConfigManager;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.XtruderConfig;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;



import com.protoplant.xtruder2.event.StepperConnectEvent;

import util.config.ConfigManager;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;




public class ConfigDetailPanel extends Composite {
	private Logger log;
	private XtruderConfig config;
	private ConfigManager<XtruderConfig> cfgMgr;
	protected Text txtMainEdit;
	protected Button btnEditCurrent;
	protected Button btnSave;
	protected Button btnApplyEdits;
	protected Text txtStatus;
	protected Label lblStatus;
	private EventBus eb;
	private StepperConfigManager scm;

	public ConfigDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FormLayout());
		
		txtMainEdit = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtMainEdit.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txtMainEdit.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtMainEdit.setTouchEnabled(true);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(100, -11);
		fd_text.top = new FormAttachment(0, 12);
		txtMainEdit.setLayoutData(fd_text);
		
		btnEditCurrent = new Button(this, SWT.NONE);
		btnEditCurrent.setTouchEnabled(true);
		fd_text.left = new FormAttachment(btnEditCurrent, 6);
		btnEditCurrent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				editCurrent();
			}
		});
		FormData fd_btnLoad = new FormData();
		fd_btnLoad.top = new FormAttachment(txtMainEdit, 0, SWT.TOP);
		fd_btnLoad.left = new FormAttachment(0, 12);
		btnEditCurrent.setLayoutData(fd_btnLoad);
		btnEditCurrent.setText("Edit Current >>");
		
		btnSave = new Button(this, SWT.NONE);
		btnSave.setTouchEnabled(true);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				save();
			}
		});
		FormData fd_btnSave = new FormData();
		fd_btnSave.top = new FormAttachment(0, 125);
		fd_btnSave.left = new FormAttachment(0, 13);
		btnSave.setLayoutData(fd_btnSave);
		btnSave.setText("Save File");
		
		btnApplyEdits = new Button(this, SWT.NONE);
		btnApplyEdits.setTouchEnabled(true);
		fd_btnSave.right = new FormAttachment(btnApplyEdits, 0, SWT.RIGHT);
		btnApplyEdits.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				applyEdits();
			}
		});
		FormData fd_btnUseEdits = new FormData();
		fd_btnUseEdits.top = new FormAttachment(0, 55);
		fd_btnUseEdits.left = new FormAttachment(btnEditCurrent, 0, SWT.LEFT);
		btnApplyEdits.setLayoutData(fd_btnUseEdits);
		btnApplyEdits.setText("Apply Edits  <<");
		
		txtStatus = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtStatus.setTouchEnabled(true);
		txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txtStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		fd_text.bottom = new FormAttachment(txtStatus, -6);
		FormData fd_text_1 = new FormData();
		fd_text_1.top = new FormAttachment(100, -52);
		fd_text_1.bottom = new FormAttachment(100, -12);
		fd_text_1.right = new FormAttachment(txtMainEdit, 0, SWT.RIGHT);
		fd_text_1.left = new FormAttachment(0, 12);
		txtStatus.setLayoutData(fd_text_1);
		
		lblStatus = new Label(this, SWT.NONE);
		FormData fd_lblStatus = new FormData();
		fd_lblStatus.bottom = new FormAttachment(100, -57);
		fd_lblStatus.right = new FormAttachment(0, 59);
		fd_lblStatus.left = new FormAttachment(0, 12);
		fd_lblStatus.top = new FormAttachment(100, -83);
		lblStatus.setLayoutData(fd_lblStatus);
		lblStatus.setText("Status:");
		
		if (injector!=null) injector.injectMembers(this);
	}
	
	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, ConfigManager<XtruderConfig> cfgMgr, StepperConfigManager scm) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.cfgMgr = cfgMgr;
		this.scm = scm;
		
	}

	@Subscribe
	public void onStepperConnect(StepperConnectEvent evt) {
		if (evt.getFunction()==StepperFunction.UNDEFINED) {
			txtStatus.setText("Undefined stepper module detected, serial number = \""+evt.getSerial()+"\"");
		}
	}
	
	protected void editCurrent() {
		try {
			eb.post(new ConfigStoreEvent());
			txtMainEdit.setText(cfgMgr.getText(config));
			txtStatus.setText("Current configuration loaded into text editor.");
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
		}		
	}
	
	protected void applyEdits() {
		try {
			cfgMgr.update(txtMainEdit.getText(), config);
			scm.buildStepperMaps();
			eb.post(new ConfigSetupEvent());
			txtStatus.setText("Current configuration updated.");
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
		}
	}

	protected void save() {
		try {
			cfgMgr.save(cfgMgr.mapConfigFromText(txtMainEdit.getText()));
			txtStatus.setText("Current configuration saved.");
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
		}
	}
	
	protected void test() {
		log.info(""+config.steppers[0].speedSetPoint);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
