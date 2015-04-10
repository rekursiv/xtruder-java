package com.protoplant.xtruder2.usb;

import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.swt.widgets.Display;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.IndicatorZeroEvent;

public class IndicatorModule extends UsbModule {
	
	private volatile boolean doZero=false;
	
	
	@Inject
	public IndicatorModule(Logger log, EventBus eb) {
		super(log, eb);
	}

	@Subscribe
	public synchronized void onZero(IndicatorZeroEvent evt) {
		doZero=true;
	}
	

	@Override
	protected synchronized byte[] encodePacket() {
		byte[] pkt = new byte[1];
		if (doZero) {
			doZero=false;
			pkt[0]=1;
		} else {
			pkt[0]=0;
		}
		return pkt;
	}
	
	@Override
	protected synchronized void decodePacket(byte[] pkt) {
        float cur = (float)extractInt16(pkt, 3)*0.002f;
        float min = (float)extractInt16(pkt, 5)*0.002f;        
        float max = (float)extractInt16(pkt, 7)*0.002f;
        IndicatorDataEvent ide = new IndicatorDataEvent(cur, min, max);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				eb.post(ide);
			}
		});
	}
	

}


//System.out.println(DatatypeConverter.printHexBinary(buf));
//System.out.println(toBinary(buf[4])+toBinary(buf[5]));

//System.out.println(String.format("%02X %02X", buf[2], buf[3]));
//System.out.println(""+buf[2]);
//System.out.println(new String(buf, "US-ASCII").substring(2, len+2));
//System.out.println("-----");
