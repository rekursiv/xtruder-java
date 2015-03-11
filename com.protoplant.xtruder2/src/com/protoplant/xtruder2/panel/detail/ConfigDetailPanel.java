package com.protoplant.xtruder2.panel.detail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.MachineState;
import com.protoplant.xtruder2.config.StepperConfigManager;
import com.protoplant.xtruder2.config.XtruderConfig;
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
import org.eclipse.swt.widgets.List;




public class ConfigDetailPanel extends Composite {
	private Logger log;
	private XtruderConfig config;
	private ConfigManager<XtruderConfig> cfgMgr;
	protected Text txtMainEdit;
	protected Button btnPull;
	protected Button btnSaveFile;
	protected Button btnNewFile;
	protected Text txtStatus;
	protected Label lblStatus;
	private EventBus eb;
	private StepperConfigManager scm;
	private List lstFiles;

	public ConfigDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FormLayout());
		
		txtMainEdit = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
//		txtMainEdit.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
//		txtMainEdit.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(100, -11);
		fd_text.top = new FormAttachment(0, 12);
		txtMainEdit.setLayoutData(fd_text);
		
		btnPull = new Button(this, SWT.NONE);
		btnPull.setTouchEnabled(true);
		fd_text.left = new FormAttachment(0, 225);
		btnPull.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
//				editCurrent();
				mergeMachineState();
			}
		});
		FormData fd_btnPull = new FormData();
		fd_btnPull.right = new FormAttachment(0, 140);
		fd_btnPull.top = new FormAttachment(txtMainEdit, 0, SWT.TOP);
		fd_btnPull.left = new FormAttachment(0, 12);
		btnPull.setLayoutData(fd_btnPull);
		btnPull.setText("Pull Machine State");
		
		btnSaveFile = new Button(this, SWT.NONE);
		btnSaveFile.setTouchEnabled(true);
		btnSaveFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
//				save();
//				test();
			}
		});
		FormData fd_btnSaveFile = new FormData();
		btnSaveFile.setLayoutData(fd_btnSaveFile);
		btnSaveFile.setText("Save File");
		
		btnNewFile = new Button(this, SWT.NONE);
		fd_btnSaveFile.top = new FormAttachment(btnNewFile, -25);
		fd_btnSaveFile.bottom = new FormAttachment(btnNewFile, 0, SWT.BOTTOM);
		btnNewFile.setTouchEnabled(true);
		btnNewFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
//				applyEdits();
				newFile();
			}
		});
		FormData fd_btnNewFile = new FormData();
		fd_btnNewFile.right = new FormAttachment(0, 90);
		fd_btnNewFile.top = new FormAttachment(0, 55);
		fd_btnNewFile.left = new FormAttachment(btnPull, 0, SWT.LEFT);
		btnNewFile.setLayoutData(fd_btnNewFile);
		btnNewFile.setText("New File...");
		
		txtStatus = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtStatus.setTouchEnabled(true);
//		txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
//		txtStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
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
		
		lstFiles = new List(this, SWT.BORDER);
		lstFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				log.info(lstFiles.getItem(lstFiles.getSelectionIndex()));
			}
		});
		FormData fd_lstFiles = new FormData();
		fd_lstFiles.right = new FormAttachment(btnNewFile, 198);
		fd_lstFiles.left = new FormAttachment(0, 12);
		fd_lstFiles.bottom = new FormAttachment(100, -292);
		fd_lstFiles.top = new FormAttachment(0, 169);
		lstFiles.setLayoutData(fd_lstFiles);
		
		Button btnTest = new Button(this, SWT.NONE);
		fd_btnSaveFile.left = new FormAttachment(0, 108);
		fd_btnSaveFile.right = new FormAttachment(0, 185);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				test();
			}
		});
		FormData fd_btnTest = new FormData();
		fd_btnTest.bottom = new FormAttachment(0, 125);
		fd_btnTest.right = new FormAttachment(0, 110);
		fd_btnTest.top = new FormAttachment(0, 100);
		fd_btnTest.left = new FormAttachment(0, 52);
		btnTest.setLayoutData(fd_btnTest);
		btnTest.setText("TEST");
		
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
	
	public void onPanelFocus() {   //  TODO:  call editCurrent()
		log.info("");
	}
	
	
	//mapper.readerForUpdating(model).readValue(text);
//	return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model).replace("\r", "");
	public void mergeMachineState() {
		eb.post(new ConfigStoreEvent());  //  update current machine state	
		ObjectMapper om = new ObjectMapper();
		try {
			JsonNode edit = om.readTree(txtMainEdit.getText().replace("\r", ""));
			JsonNode ms = om.valueToTree(scm.getMachineState());
			edit = merge(edit, ms);
			txtMainEdit.setText(cfgMgr.getText(edit));
			txtStatus.setText("");
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
		}
	}
	
	protected void newFile() {
		eb.post(new ConfigStoreEvent());
		try {
			txtMainEdit.setText(cfgMgr.getText(scm.getMachineState()));
			txtStatus.setText("");
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
		}
	}
	
	
	// http://stackoverflow.com/questions/9895041/merging-two-json-documents-using-jackson
	public JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
	    Iterator<String> fieldNames = updateNode.fieldNames();
	    while (fieldNames.hasNext()) {
	        String fieldName = fieldNames.next();
	        JsonNode jsonNode = mainNode.get(fieldName);
	        // if field exists and is an embedded object
	        if (jsonNode != null && jsonNode.isObject()) {
	            merge(jsonNode, updateNode.get(fieldName));
	        }
	        else {
	            if (mainNode instanceof ObjectNode) {
	                // Overwrite field
	                JsonNode value = updateNode.get(fieldName);
	                ((ObjectNode) mainNode).put(fieldName, value);
	            }
	        }
	    }
	    return mainNode;
	}
	
	public void test_files() {
		lstFiles.removeAll();
		File cfgDir = new File(System.getProperty("user.dir")+"/config/");
		File[] fileList = cfgDir.listFiles();
		for (File file : fileList) {
			lstFiles.add(stripExt(file.getName()));
		}
	}
	
	
	protected void test() {
		eb.post(new ConfigStoreEvent());
		try {
			txtMainEdit.setText(cfgMgr.getText(scm.getMachineState()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	
	protected String stripExt(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if (pos==-1) return fileName;
		return fileName.substring(0, pos);
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
//			scm.buildStepperMaps();
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

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
