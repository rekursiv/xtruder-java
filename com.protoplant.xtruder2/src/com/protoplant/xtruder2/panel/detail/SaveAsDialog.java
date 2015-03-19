package com.protoplant.xtruder2.panel.detail;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SaveAsDialog extends TitleAreaDialog {

	private Text txtFileName;
	private String fileName;

	public SaveAsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Save text in editor to a file");
		setMessage("Please enter a name for the file:", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);
		txtFileName = new Text(container, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return area;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(320, 200);
	}

	@Override
	protected void okPressed() {
		fileName = txtFileName.getText();   //  TODO:  check file name??
		super.okPressed();
	}
	
	public String getFileName() {
		return fileName;
	}
}


