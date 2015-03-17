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
import com.protoplant.xtruder2.config.StepperConfigManager;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.ConfigFileSelectEvent;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;
import com.protoplant.xtruder2.event.StepperConnectEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
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
	private StepperConfigManager scm;
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
		fd_btnPull.right = new FormAttachment(0, 195);
		fd_btnPull.top = new FormAttachment(0, 12);
		fd_btnPull.left = new FormAttachment(0, 12);
		btnPull.setLayoutData(fd_btnPull);
		btnPull.setText("Pull Machine State");
		
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
		fd_btnSaveAs.top = new FormAttachment(btnSave, -35);
		fd_btnSaveAs.bottom = new FormAttachment(btnSave, 0, SWT.BOTTOM);
		fd_btnSaveAs.left = new FormAttachment(btnPull, -80);
		fd_btnSaveAs.right = new FormAttachment(btnPull, 0, SWT.RIGHT);
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
		
		lstFiles = new org.eclipse.swt.widgets.List(this, SWT.BORDER);
		lstFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				onFileSelect();
			}
		});
		FormData fd_lstFiles = new FormData();
		fd_lstFiles.bottom = new FormAttachment(txtMainEdit, 0, SWT.BOTTOM);
		fd_lstFiles.right = new FormAttachment(0, 210);
		fd_lstFiles.left = new FormAttachment(0, 12);
		fd_lstFiles.top = new FormAttachment(0, 315);
		lstFiles.setLayoutData(fd_lstFiles);
		
		Button btnTest = new Button(this, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				test();
			}
		});
		FormData fd_btnTest = new FormData();
		fd_btnTest.left = new FormAttachment(0, 77);
		fd_btnTest.right = new FormAttachment(0, 135);
		fd_btnTest.bottom = new FormAttachment(20, 25);
		fd_btnTest.top = new FormAttachment(20);
		btnTest.setLayoutData(fd_btnTest);
		btnTest.setText("TEST");
		
		Button btnDelete = new Button(this, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				deleteCurFile();
			}
		});
		FormData fd_btnDelete = new FormData();
		fd_btnDelete.left = new FormAttachment(lstFiles, -82);
		fd_btnDelete.right = new FormAttachment(lstFiles, 0, SWT.RIGHT);
		btnDelete.setLayoutData(fd_btnDelete);
		btnDelete.setText("Delete");
		
		Button btnUse = new Button(this, SWT.NONE);
		fd_btnSave.right = new FormAttachment(btnUse, 0, SWT.RIGHT);
		fd_btnDelete.bottom = new FormAttachment(btnUse, 30);
		fd_btnDelete.top = new FormAttachment(btnUse, 0, SWT.TOP);
		btnUse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				useConfigInView();
			}
		});
		FormData fd_btnUse = new FormData();
		fd_btnUse.right = new FormAttachment(0, 95);
		fd_btnUse.left = new FormAttachment(0, 13);
		fd_btnUse.bottom = new FormAttachment(0, 300);
		fd_btnUse.top = new FormAttachment(0, 270);
		btnUse.setLayoutData(fd_btnUse);
		btnUse.setText("Use");
		

		
		if (injector!=null) injector.injectMembers(this);
	}
	

	protected void onEdit() {
		if (isMainEditReady && lstFiles.getSelectionIndex()==configIndexInUse) {
			lstFiles.setItem(lstFiles.getSelectionIndex(), ">* "+configNameInUse);	
			System.out.println("****");
			isMainEditModified=true;
		}

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
	
	protected void test() {
//		lstFiles.
	}
	
	public void onPanelFocus() {
		readInUsefile();
		updateFileList();
//		lstFiles.select(0);
//		onFileSelect();
//		test();
	}
	
	private void readInUsefile() {
		try {
			configNameInUse = new String(Files.readAllBytes(buildPath(configDir+"inuse")), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeInUseFile() {
		try {
			Files.write(buildPath(configDir+"inuse"), configNameInUse.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	protected void onSave() {
		if (configNameInUse!=null) saveFile(configNameInUse);
	}
	
	protected void onSaveAs() {
		SaveAsDialog sad = new SaveAsDialog(this.getShell());
		sad.create();
		if (sad.open()==Window.OK) {
			saveFile(sad.getFileName());
		} else {
			System.out.println("Cancel");	
		}
	}
	
	protected void saveFile(String fileName) {
		try {
//			txtMainEdit.getText();
			String textToSave = txtMainEdit.getText().replace("\r", "");
			List<String> lines = Arrays.asList(textToSave.split("\\n"));
			
			//  TODO:  parse/apply to machine
			
			Files.write(buildPath(settingsDir+fileName+".js"), lines, StandardCharsets.UTF_8);
			isMainEditModified=false;
			txtStatus.setText("Current configuration saved.");
			updateFileList();
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
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
			txtMainEdit.setEditable(true);
			isMainEditModified=false;
			writeInUseFile();
			eb.post(new ConfigFileSelectEvent(configNameInView));
		}
	}


	public void onFileSelect() {
		int sel = lstFiles.getSelectionIndex();
		if (sel>=0) {
			isMainEditReady=false;
			if (configIndexInUse==configIndexInView) {  // TODO:  check for edit??
				configTextInUse = txtMainEdit.getText();
				System.out.println("^^^");
			}
			
			configIndexInView = sel;
			if (lstFiles.getItem(sel).startsWith(">")) {
				configNameInView=lstFiles.getItem(sel).substring(2);
			} else {
				configNameInView=lstFiles.getItem(sel);
			}
			
			if (sel==configIndexInUse) {
				if (configTextInUse!=null) {
					txtMainEdit.setText(configTextInUse);
				} else {
					loadFile(settingsDir+configNameInView+".js");
				}
				txtMainEdit.setEditable(true);
			}
			else {
				txtMainEdit.setEditable(false);
				loadFile(settingsDir+configNameInView+".js");
			}
			isMainEditReady=true;
		}
	}
	
	public void deleteCurFile() {
		MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
		mb.setMessage("Really delete '"+configNameInView+"'?");
		mb.setText("Config File Delete");
		int response = mb.open();
		if (response==SWT.YES) {
			try {
				Files.delete(buildPath(settingsDir+configNameInView+".js"));
				updateFileList();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//mapper.readerForUpdating(model).readValue(text);
//	return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model).replace("\r", "");
	public void mergeMachineState() {
		extractJson();
		eb.post(new ConfigStoreEvent());  //  update current machine state	
		ObjectMapper om = new ObjectMapper();
		try {
			JsonNode edit = om.readTree(curJson);
			JsonNode ms = om.valueToTree(scm.getMachineState());
			edit = merge(edit, ms);
			txtMainEdit.setText(curHeader+cfgMgr.getText(edit));
			txtStatus.setText("");
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
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
		System.out.println("'"+curHeader+"'");
		System.out.println("");
		System.out.println("'"+curJson+"'");
	}
	
	protected void newFile() {            //  FIXME  - use merge
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
	
	public void updateFileList() {
		lstFiles.removeAll();
		File cfgDir = new File(System.getProperty("user.dir")+settingsDir);
		File[] fileList = cfgDir.listFiles();

		for (File file : fileList) {
			if (file.isFile()&&file.getName().endsWith(".js")) {
				String nameRoot = stripExt(file.getName());
				if (nameRoot.equals(configNameInUse)) {
					configIndexInUse = lstFiles.getItemCount();
					nameRoot = "> "+nameRoot;
				}
				lstFiles.add(nameRoot);
			}
		}
		
		lstFiles.select(configIndexInUse);
		configIndexInView=-1;
		onFileSelect();
		useConfigInView();

	}
	
	protected void loadFile(String fileName) {
		StringBuilder sb = new StringBuilder();
		try {
			for (String line : Files.readAllLines(buildPath(fileName), StandardCharsets.UTF_8)) {
				if (sb.length()>0) sb.append("\n");
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();   ///  FIXME
		}
		txtMainEdit.setText(sb.toString());
	}
	

	
	public Path buildPath(String fileName) {
		String pathStr = System.getProperty("user.dir")+fileName;
		Path path = Paths.get(pathStr);
		return path;
	}
	
	protected String stripExt(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if (pos==-1) return fileName;
		return fileName.substring(0, pos);
	}
	
	
	
	
	
	protected void _editCurrent() {
		try {
			eb.post(new ConfigStoreEvent());
			txtMainEdit.setText(cfgMgr.getText(config));
			txtStatus.setText("Current configuration loaded into text editor.");
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
		}		
	}
	
	protected void _applyEdits() {
		try {
			cfgMgr.update(txtMainEdit.getText(), config);
//			scm.buildStepperMaps();
			eb.post(new ConfigSetupEvent());
			txtStatus.setText("Current configuration updated.");
		} catch (Exception e) {
			txtStatus.setText(e.getLocalizedMessage());
		}
	}

	protected void _save() {
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
