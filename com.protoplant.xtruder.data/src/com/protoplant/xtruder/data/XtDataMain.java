package com.protoplant.xtruder.data;

import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class XtDataMain {

	private static XtDataMain instance;
	private Shell shell;
	
	public static void main(String[] args) {

		instance = new XtDataMain();
		instance.init();
		instance.mainLoop();
		
	}

	private void init() {

		shell = new Shell();
		shell.setSize(1200, 800);
		shell.setText("Proto-Pasta Statistical Process Control Data Plotter V1.0");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		new DataView(shell);

		shell.open();
		shell.layout();
		
	}

	private void mainLoop() {
		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
