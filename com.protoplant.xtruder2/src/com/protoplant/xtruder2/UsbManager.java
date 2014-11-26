package com.protoplant.xtruder2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Display;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class UsbManager extends Thread {

	private static final int IO_REFRESH_PERIOD = 200;
	private static final int CONNECT_REFRESH_PERIOD = 2000;
	
	protected static final int vendorId = 0x2047;
//	protected static final int vendorId = 0x80ee;
	
	private Logger log;
	private Injector injector;
	private Map<String, UsbModule> modules;
	private int ioRefreshCount;

	@Inject
	public UsbManager(Logger log, Injector injector) {
		this.log = log;
		this.injector = injector;
	}
	
	public void init() {
		if (!ClassPathLibraryLoader.loadNativeHIDLibrary()) {
			log.warning("Failed to load native HID library.");
		}
		
		listDevs();
		
		ioRefreshCount = 0;
		modules = new TreeMap<String, UsbModule>();
		refreshConnectionAsync();
        start();
		
	}
	
	public void release() {
		interrupt();
		for (UsbModule mod : modules.values()) {
			mod.disconnect();
		}
		try {
			HIDManager.getInstance().release();
		} catch (IOException e) {
			log.log(Level.WARNING, "", e);
		}
	}
	
	public void listDevs() {
		try {
			HIDDeviceInfo[] devs = HIDManager.getInstance().listDevices();
			if (devs!=null) {
				for (HIDDeviceInfo info : devs) {
					if (info.getVendor_id()==vendorId) log.info(info.toString());
				}	
			}

		} catch (IOException e) {
			log.log(Level.WARNING, "", e);
		}
	}
	
	
	private void refreshConnectionAsync() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				refreshConnections();
			}
		});
	}
	
	private void refreshConnections() {
//		log.info(modules.size()+"");
		Iterator<Entry<String, UsbModule>> it = modules.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, UsbModule> entry = (Map.Entry<String, UsbModule>)it.next();
//			System.out.println(entry.getKey() + " = " + entry.getValue().isConnected());
			if (!entry.getValue().isConnected()) it.remove();
		}
//		log.info(modules.size()+"");

		try {
			HIDDeviceInfo[] devs = HIDManager.getInstance().listDevices();
			if (devs!=null) {
				for (HIDDeviceInfo info : devs) {
					if (info.getVendor_id()==vendorId&&info.getSerial_number()!=null) {
						if (!modules.containsKey(info.getSerial_number())) {  // check to see if this device is already mapped
							UsbModule mod = null;
							switch (info.getProduct_id()) {
							case 0x03E0:
								mod = injector.getInstance(IndicatorModule.class);
								break;
							case 0x03E1:
								mod = injector.getInstance(AnalogModule.class);
								break;
							case 0x03E2:  
								mod = injector.getInstance(StepperModule.class);
								break;
							}

							if (mod!=null) {
								mod.connect(info);
								if (mod.isConnected()&&info.getSerial_number()!=null) {
									log.info("*USB* "+info.getProduct_string()+":"+info.getSerial_number());
									log.info(info.toString());
									modules.put(info.getSerial_number(), mod);
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "", e);
		}
	}
	


	@Override
	public void run() {
		while (isAlive()) {
			
			if (ioRefreshCount*IO_REFRESH_PERIOD>CONNECT_REFRESH_PERIOD) {
				refreshConnectionAsync();
				ioRefreshCount=0;
			} else {
				++ioRefreshCount;
			}
			
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					for (UsbModule mod : modules.values()) {
						mod.refreshWrite();
					}
				}
			});

			try {
				Thread.sleep(IO_REFRESH_PERIOD);
			} catch (InterruptedException e) {
				log.info("thread interrupt");
				return;
			}

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					for (UsbModule mod : modules.values()) {
						mod.refreshRead();
					}
				}
			});

		}
	}

}
