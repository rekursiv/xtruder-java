package com.protoplant.xtruder2;

import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

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
//		pid = 0x03E0;
	}

	@Subscribe
	public void onZero(IndicatorZeroEvent evt) {
		doZero=true;
	}
	

	@Override
	protected byte[] encodePacket() {
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
	protected void decodePacket(byte[] pkt) {
        float cur = (float)extractInt16(pkt, 3)*0.002f;
        float min = (float)extractInt16(pkt, 5)*0.002f;        
        float max = (float)extractInt16(pkt, 7)*0.002f;
        IndicatorDataEvent ide = new IndicatorDataEvent(cur, min, max);
        eb.post(ide);
       
//      log.info(bufLen+":"+buf[1]+":"+buf[2]+":"+buf[3]+"     "+extractInt16(buf, 4)+":"+extractInt16(buf, 6)+":"+extractInt16(buf, 8));
//        log.info(DatatypeConverter.printHexBinary(pkt));
        
	}
	

}


//System.out.println(DatatypeConverter.printHexBinary(buf));
//System.out.println(toBinary(buf[4])+toBinary(buf[5]));

//System.out.println(String.format("%02X %02X", buf[2], buf[3]));
//System.out.println(""+buf[2]);
//System.out.println(new String(buf, "US-ASCII").substring(2, len+2));
//System.out.println("-----");
