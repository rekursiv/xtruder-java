package com.protoplant.xtruder2;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.protoplant.xtruder2.panel.RootPanel;
import com.protoplant.xtruder2.usb.UsbManager;



public class Xtruder2App {
	
	
	public static void main(String[] args) {
		System.out.println("Xtruder2App Entry Point");
//		runRemote();
		System.getProperties().setProperty("java.util.logging.config.class", "util.logging.LogSetup");
		Xtruder2App instance = new Xtruder2App();
		instance.init();
//		instance.test();
		System.out.println("Bye.");
		System.exit(0);
	}
	
	private static void runRemote() {
		if (System.getProperty("os.name").contains("Windows")) {
			System.out.println("Detected Windows, will attempt to run on remote system...");
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", "runremote.jar");
			pb.directory(new File("C:/projects/eclipse_workspace/_deploy/runremote/"));
			pb.inheritIO();
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	protected void init() {
		XtruderShell shell = new XtruderShell();
	
		// splash screen
		shell.setSize(600, 0);
		shell.setText("Loading Protoplant Xtruder, please wait...");
		shell.open();

		Injector injector = Guice.createInjector(new XtruderGuice());

		new RootPanel(shell, injector);
		
		StepperConfigManager scm = injector.getInstance(StepperConfigManager.class);
		
		UsbManager usb = injector.getInstance(UsbManager.class);
		usb.init();
		
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
