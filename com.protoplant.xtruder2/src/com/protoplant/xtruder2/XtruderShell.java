package com.protoplant.xtruder2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;


public class XtruderShell extends Shell {

	public void init() {
		Display display = Display.getDefault();
		setBackgroundMode(SWT.INHERIT_FORCE);
		open();
		layout();
		System.out.println("Entering main loop...");
		while (!isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
//		try {Thread.sleep(200);} catch (InterruptedException e) {}  // give things a chance to shut down
		SWTResourceManager.dispose();
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
}