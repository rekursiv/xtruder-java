package com.protoplant.xtruder2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.protoplant.xtruder2.panel.RootPanel;



public class Xtruder2App {
	
	
	public static void main(String[] args) {
		System.getProperties().setProperty("java.util.logging.config.class", "util.logging.LogSetup");
		Xtruder2App instance = new Xtruder2App();
		instance.init();
//		instance.test();
		System.out.println("Bye.");
		System.exit(0);
	}
	

	protected void init() {
		XtruderShell shell = new XtruderShell();
	
		// splash screen
		shell.setSize(600, 0);
		shell.setText("Loading Protoplant Xtruder, please wait...");
		shell.open();

		
		Injector injector = Guice.createInjector(new XtruderGuice());
		
		
		/////////
		
		StepperConfigManager scm = injector.getInstance(StepperConfigManager.class);
		
		/////////
		
		UsbManager usb = injector.getInstance(UsbManager.class);
		usb.init();

		
		new RootPanel(shell, injector);
		
		// Dell touchscreen is 1600 X 900
//		shell.setBounds(0, 0, 1600, 900);
		shell.setSize(1600, 900);
		shell.setText("Protoplant Xtruder V2.x");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		
		shell.init();  // main loop
		
		
		usb.release();


	}

	
	
	
	protected void test() {

		
	}

}
