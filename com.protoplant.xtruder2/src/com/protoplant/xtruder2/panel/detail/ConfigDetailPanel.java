package com.protoplant.xtruder2.panel.detail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import util.config.ConfigManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.MachineState;
import com.protoplant.xtruder2.config.StepperConfigManager;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.ConfigFileSelectEvent;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;
import com.protoplant.xtruder2.event.StepperConnectEvent;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.List;




public class ConfigDetailPanel extends Composite {
	
	private static final String configDir = "/config/";
	private static final String settingsDir = "/config/settings/";
	
	private Logger log;
	private XtruderConfig config;
	private ConfigManager<XtruderConfig> cfgMgr;
	protected Text txtMainEdit;
	protected Button btnPull;
	protected Button btnSave;
	protected Button btnSaveAs;
	protected Text txtStatus;
	private EventBus eb;
	private org.eclipse.swt.widgets.List lstFiles;
	
	private String configNameInView;
	private int configIndexInView;
	
	private String configNameInUse;
	private int configIndexInUse;
	
	private String configTextInUse;
	private String curJson;
	private String curHeader;
	
	private boolean isMainEditReady;
	private boolean isMainEditModified;
	private Button btnRevert;
	private Button btnDelete;
	private Button btnUse;
	private MachineState ms;
	

	public ConfigDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FormLayout());
		
		txtMainEdit = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtMainEdit.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				onEdit();
			}
		});
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
		fd_btnPull.top = new FormAttachment(txtMainEdit, 0, SWT.TOP);
		fd_btnPull.left = new FormAttachment(0, 12);
		btnPull.setLayoutData(fd_btnPull);
		btnPull.setText("Pull");
		
		btnSave = new Button(this, SWT.NONE);
		fd_btnPull.bottom = new FormAttachment(0, 50);
		btnSave.setTouchEnabled(true);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				onSave();
			}
		});
		FormData fd_btnSave = new FormData();
		fd_btnSave.left = new FormAttachment(btnPull, 0, SWT.LEFT);
		btnSave.setLayoutData(fd_btnSave);
		btnSave.setText("Save");
		
		btnSaveAs = new Button(this, SWT.NONE);
		fd_btnSave.top = new FormAttachment(0, 65);
		fd_btnSave.bottom = new FormAttachment(0, 100);
		btnSaveAs.setTouchEnabled(true);
		btnSaveAs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				onSaveAs();
			}
		});
		FormData fd_btnSaveAs = new FormData();
		fd_btnSaveAs.top = new FormAttachment(0, 65);
		fd_btnSaveAs.bottom = new FormAttachment(0, 100);
		fd_btnSaveAs.right = new FormAttachment(0, 195);
		btnSaveAs.setLayoutData(fd_btnSaveAs);
		btnSaveAs.setText("Save as...");
		
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
		
		lstFiles = new org.eclipse.swt.widgets.List(this, SWT.BORDER | SWT.V_SCROLL);
		lstFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				onFileSelect();
			}
		});
		FormData fd_lstFiles = new FormData();
		fd_lstFiles.right = new FormAttachment(txtMainEdit, -6);
		fd_lstFiles.bottom = new FormAttachment(txtMainEdit, 0, SWT.BOTTOM);
		fd_lstFiles.left = new FormAttachment(0, 12);
		fd_lstFiles.top = new FormAttachment(0, 195);
		lstFiles.setLayoutData(fd_lstFiles);
		
		btnRevert = new Button(this, SWT.NONE);
		btnRevert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				onRevert();
			}
		});
		FormData fd_btnRevert = new FormData();
		fd_btnRevert.left = new FormAttachment(btnSaveAs, 0, SWT.LEFT);
		fd_btnRevert.top = new FormAttachment(txtMainEdit, 0, SWT.TOP);
		fd_btnRevert.bottom = new FormAttachment(btnPull, 0, SWT.BOTTOM);
		fd_btnRevert.right = new FormAttachment(btnSaveAs, 0, SWT.RIGHT);
		btnRevert.setLayoutData(fd_btnRevert);
		btnRevert.setText("Revert");
		
		btnDelete = new Button(this, SWT.NONE);
		fd_btnSaveAs.left = new FormAttachment(btnDelete, 0, SWT.LEFT);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				deleteFileInView();
			}
		});
		FormData fd_btnDelete = new FormData();
		fd_btnDelete.right = new FormAttachment(0, 197);
		fd_btnDelete.left = new FormAttachment(0, 115);
		btnDelete.setLayoutData(fd_btnDelete);
		btnDelete.setText("Delete");
		
		btnUse = new Button(this, SWT.NONE);
		fd_btnDelete.top = new FormAttachment(0, 136);
		fd_btnDelete.bottom = new FormAttachment(0, 176);
		fd_btnPull.right = new FormAttachment(btnUse, 0, SWT.RIGHT);
		fd_btnSave.right = new FormAttachment(btnUse, 0, SWT.RIGHT);
		btnUse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				useConfigInView();
			}
		});
		FormData fd_btnUse = new FormData();
		fd_btnUse.left = new FormAttachment(lstFiles, 0, SWT.LEFT);
		fd_btnUse.right = new FormAttachment(0, 100);
		fd_btnUse.bottom = new FormAttachment(0, 176);
		fd_btnUse.top = new FormAttachment(0, 136);
		btnUse.setLayoutData(fd_btnUse);
		btnUse.setText("Use");
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(0, 117);
		fd_label.right = new FormAttachment(btnDelete, 0, SWT.RIGHT);
		fd_label.top = new FormAttachment(0, 115);
		fd_label.left = new FormAttachment(0, 12);
		label.setLayoutData(fd_label);
		

		
		if (injector!=null) injector.injectMembers(this);
	}
	

	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, ConfigManager<XtruderConfig> cfgMgr, MachineState ms) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.cfgMgr = cfgMgr;
		this.ms = ms;
		
		readInUsefile();
		updateFileList();
	}

	@Subscribe
	public void onStepperConnect(StepperConnectEvent evt) {
		if (evt.getFunction()==StepperFunction.UNDEFINED) {
			txtStatus.setText("Undefined stepper module detected, serial number = \""+evt.getSerial()+"\"");
		}
	}
	
	protected void onPanelFocus() {

	}
	
	protected void onRevert() {
		if (isMainEditModified) {
			MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mb.setMessage("Really revert '"+configNameInUse+"' to previously saved values,\noverwriting your current changes?");
			mb.setText("Config File Revert");
			int response = mb.open();
			if (response==SWT.YES) {
				revertFileInUse();
			}
		} else {
			revertFileInUse();  // file showing should be identical to file on disk, but this can be used for debugging...
		}
	}
	
	protected void revertFileInUse() {
		configTextInUse=null;
		loadFileForViewing();
		resetEditModified();
	}

	protected void onEdit() {
		if (!isMainEditModified && isMainEditReady && lstFiles.getSelectionIndex()==configIndexInUse) {
			lstFiles.setItem(lstFiles.getSelectionIndex(), ">* "+configNameInUse);	
			System.out.println("****");
			isMainEditModified=true;
		}
	}
	
	protected void onFileSelect() {
		int sel = lstFiles.getSelectionIndex();
		if (sel>=0) {
			
			if (configIndexInUse==configIndexInView && isMainEditModified) {
				configTextInUse = txtMainEdit.getText();
				System.out.println("^^^");
			}
			
			configIndexInView = sel;
			if (lstFiles.getItem(sel).startsWith(">")) {
				configNameInView=lstFiles.getItem(sel).substring(3);
			} else {
				configNameInView=lstFiles.getItem(sel);
			}
			
			if (sel==configIndexInUse) {
				if (configTextInUse!=null) {
					isMainEditReady=false;
					txtMainEdit.setText(configTextInUse);
					isMainEditReady=true;
				} else {
					System.out.println("-------   '"+configNameInView+"'");
					loadFileForViewing();
				}
				setUseMode();
			}
			else {
				setViewMode();
				loadFileForViewing();
			}
		}
	}
	
	
	protected void setViewMode() {
		txtMainEdit.setEditable(false);
		btnPull.setEnabled(false);
		btnRevert.setEnabled(false);
		btnSave.setEnabled(false);
		btnSaveAs.setEnabled(false);
		btnUse.setEnabled(true);
		btnDelete.setEnabled(true);
	}
	
	protected void setUseMode() {
		txtMainEdit.setEditable(true);
		btnPull.setEnabled(true);
		btnRevert.setEnabled(true);
		btnSave.setEnabled(true);
		btnSaveAs.setEnabled(true);
		btnUse.setEnabled(false);
		btnDelete.setEnabled(false);
	}
	
	protected void onSave() {
		if (configNameInUse!=null) {
			applyEdits();
			saveFileInUse();
		}
	}
	
	protected void onSaveAs() {
		SaveAsDialog sad = new SaveAsDialog(this.getShell());
		sad.create();
		if (sad.open()==Window.OK) {
			configNameInUse=sad.getFileName();
			configNameInView=sad.getFileName();
			applyEdits();
			saveFileInUse();
			updateFileList();
		} else {
			System.out.println("Cancel");	
		}
	}
	
	
	protected void saveFileInUse() {
		try {
			String textToSave = txtMainEdit.getText().replace("\r", "");
			List<String> lines = Arrays.asList(textToSave.split("\\n"));
			Files.write(buildPath(buildFileName()), lines, StandardCharsets.UTF_8);
			resetEditModified();
//			txtStatus.setText("Current configuration saved.");
		} catch (Exception e) {
			setStatusError(e);
		}
	}
	
	protected void resetEditModified() {
		lstFiles.setItem(lstFiles.getSelectionIndex(), ">  "+configNameInUse);	
		isMainEditModified=false;
	}

	protected void deleteFileInView() {
		MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
		mb.setMessage("Really delete '"+configNameInView+"'?");
		mb.setText("Config File Delete");
		int response = mb.open();
		if (response==SWT.YES) {
			try {
				Files.delete(buildPath(buildFileName()));
				updateFileList();
			} catch (IOException e) {
				setStatusError(e);
			}
		}
	}
	
	
	protected void useConfigInView() {
		if (lstFiles.getSelectionIndex()!=configIndexInUse) {
			if (isMainEditModified) {
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				mb.setMessage("Discard changes to '"+configNameInUse+"'?");
				mb.setText("Use '"+configNameInView+"'");
				int response = mb.open();
				if (response==SWT.NO) return;
			}
			lstFiles.setItem(configIndexInUse, configNameInUse);	
			configIndexInUse=lstFiles.getSelectionIndex();
			configNameInUse=configNameInView;
			lstFiles.setItem(lstFiles.getSelectionIndex(), ">  "+configNameInUse);	
			setUseMode();
			isMainEditModified=false;
			writeInUseFile();
			log.info("");
		}
		applyEdits();
		eb.post(new ConfigFileSelectEvent(configNameInView));
	}

	protected void readInUsefile() {
		try {
			configNameInUse = new String(Files.readAllBytes(buildPath(configDir+"inuse")), "UTF-8");
		} catch (IOException e) {
			setStatusError(e);
		}
	}

	protected void writeInUseFile() {
		try {
			Files.write(buildPath(configDir+"inuse"), configNameInUse.getBytes());
		} catch (IOException e) {
			setStatusError(e);
		}
	}

	protected void applyEdits() {
		try {
			extractJson();

			ObjectMapper om = new ObjectMapper();
			JsonNode edit = om.readTree(curJson);
			isMainEditReady=false;
			txtMainEdit.setText(curHeader+cfgMgr.getText(edit));
			isMainEditReady=true;
			JsonNode main = om.valueToTree(config);

			main = merge(main, edit);
//			System.out.println(cfgMgr.getText(main));
			cfgMgr.update(cfgMgr.getText(main), config);
			eb.post(new ConfigSetupEvent());
			
			txtStatus.setText("Current configuration updated.");
		} catch (Exception e) {
			setStatusError(e);
		}
	}


	protected void mergeMachineState() {
		extractJson();
		eb.post(new ConfigStoreEvent());  //  update current machine state	
		ObjectMapper om = new ObjectMapper();
		try {
			JsonNode editNode = om.readTree(curJson);
			JsonNode msNode = om.valueToTree(ms);
			editNode = merge(editNode, msNode);
			txtMainEdit.setText(curHeader+cfgMgr.getText(editNode));
			txtStatus.setText("");
		} catch (Exception e) {
			setStatusError(e);
		}
	}
	
	protected void extractJson() {
		curJson = "{}";
		curHeader = txtMainEdit.getText().replace("\r", "");
		int startPos = curHeader.indexOf('{');
		if (startPos>=0) {
			curJson=curHeader.substring(startPos);
			curHeader=curHeader.substring(0, startPos);
		}
//		System.out.println("'"+curHeader+"'");
//		System.out.println("");
//		System.out.println("'"+curJson+"'");
	}
	
	
	protected void updateFileList() {
		lstFiles.removeAll();
		if (Files.exists(buildPath(configDir+"system.js"))) {
			if (configNameInUse.equals("[SYSTEM]")) {
				configIndexInUse = 0;
				lstFiles.add(">  [SYSTEM]");
			} else {
				lstFiles.add("[SYSTEM]");
			}
		}
		File cfgDir = new File(System.getProperty("user.dir")+settingsDir);
		File[] fileList = cfgDir.listFiles();
		for (File file : fileList) {
			if (file.isFile()&&file.getName().endsWith(".js")) {
				String nameRoot = stripExt(file.getName());
				if (nameRoot.equals(configNameInUse)) {
					configIndexInUse = lstFiles.getItemCount();
					nameRoot = ">  "+nameRoot;
				}
				lstFiles.add(nameRoot);
			}
		}
		
		lstFiles.select(configIndexInUse);
		configIndexInView=-1;
		onFileSelect();
		useConfigInView();
	}
	
	protected void loadFileForViewing() {
		StringBuilder sb = new StringBuilder();
		try {
			for (String line : Files.readAllLines(buildPath(buildFileName()), StandardCharsets.UTF_8)) {
				if (sb.length()>0) sb.append("\n");
				sb.append(line);
			}
		} catch (IOException e) {
			setStatusError(e);
		}
		isMainEditReady=false;
		txtMainEdit.setText(sb.toString());
		isMainEditReady=true;
	}
	
	
	protected void setStatusError(Throwable e) {   //  todo:  red color bg
		txtStatus.setText(e.getLocalizedMessage());
		log.log(Level.WARNING, "", e);
//		e.printStackTrace();
	}
	

	// http://stackoverflow.com/questions/9895041/merging-two-json-documents-using-jackson
	protected JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
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
	
	protected String buildFileName() {
		if (configNameInView.equals("[SYSTEM]")) {
			return configDir+"system.js";
		} else {
			return settingsDir+configNameInView+".js";
		}	
	}
	
	protected Path buildPath(String fileName) {
		String pathStr = System.getProperty("user.dir")+fileName;
		Path path = Paths.get(pathStr);
		return path;
	}
	
	protected String stripExt(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if (pos==-1) return fileName;
		return fileName.substring(0, pos);
	}
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
