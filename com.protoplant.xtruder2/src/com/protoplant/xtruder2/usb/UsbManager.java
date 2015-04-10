package com.protoplant.xtruder2.usb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.swt.widgets.Display;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.UsbStatusEvent;

@Singleton
public class UsbManager extends Thread {

	public static final int IO_REFRESH_PERIOD = 100;
	public static final int CONNECT_REFRESH_PERIOD = 2000;
	
	protected static final int vendorId = 0x2047;
	
	private Logger log;
	private EventBus eb;
	private Injector injector;
	private Map<String, UsbModule> modules;
	private int ioRefreshCount;


	@Inject
	public UsbManager(Logger log, EventBus eb, Injector injector) {
		this.log = log;
		this.eb = eb;
		this.injector = injector;
	}
	
	public void init() {
		if (!ClassPathLibraryLoader.loadNativeHIDLibrary()) {
			log.warning("Failed to load native HID library.");
		}
		
		ioRefreshCount = 0;
		modules = new TreeMap<String, UsbModule>();
		refreshConnections();
        start();
	}
	
	public void release() {
		interrupt();
		for (UsbModule mod : modules.values()) {
			mod.release();
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
	
	private void refreshConnections() {
//		System.out.println(modules.toString());
//		log.info(modules.size()+"");
		Iterator<Entry<String, UsbModule>> it = modules.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, UsbModule> entry = (Map.Entry<String, UsbModule>)it.next();
//			System.out.println(entry.getKey() + " = " + entry.getValue().isConnected());
			if (!entry.getValue().isConnected()) {
				entry.getValue().release();
				it.remove();
				postUsbStatusToUiThread(new UsbStatusEvent(modules.size()));
			}
		}
//		log.info(modules.size()+"");

		try {
			HIDDeviceInfo[] devs = HIDManager.getInstance().listDevices();
			if (devs!=null) {
				for (HIDDeviceInfo info : devs) {
//					log.info(info.toString());
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
							case 0x0301:   //  TEST
								mod = injector.getInstance(TestModule.class);
								break;
							}

							if (mod!=null) {
								mod.connect(info);
								if (mod.isConnected()&&info.getSerial_number()!=null) {
									prettyPrintInfo(info);
									modules.put(info.getSerial_number(), mod);
									postUsbStatusToUiThread(new UsbStatusEvent(modules.size()));
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
	
	
	public void postUsbStatusToUiThread(UsbStatusEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				eb.post(event);
			}
		});
	}
	

	@Override
	public void run() {
		while (isAlive()) {

			if (ioRefreshCount*IO_REFRESH_PERIOD>CONNECT_REFRESH_PERIOD) {
				refreshConnections();   // Async
				ioRefreshCount=0;
			} else {
				++ioRefreshCount;
			}

			for (UsbModule mod : modules.values()) {
				mod.refreshWrite();
			}

			try {
				Thread.sleep(IO_REFRESH_PERIOD);
			} catch (InterruptedException e) {
				log.info("thread interrupt");
				return;
			}

			for (UsbModule mod : modules.values()) {
				mod.refreshRead();
			}

		}
	}
	
	private void prettyPrintInfo(HIDDeviceInfo info) {
		StringBuilder sb = new StringBuilder();
		sb.append("*USB* <Vendor: ");
		sb.append(info.getManufacturer_string()+", ");
		sb.append(String.format("0x%x", info.getVendor_id()));
		sb.append(">  <Product: ");
		sb.append(info.getProduct_string()+", ");
		sb.append(String.format("0x%x", info.getProduct_id()));
		sb.append(">  <Serial: ");
		sb.append(info.getSerial_number());
		sb.append(">  <Version: ");
		sb.append(info.getRelease_number());
		sb.append(">");
		log.info(sb.toString());
	}

}
