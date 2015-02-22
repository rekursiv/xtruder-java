package com.protoplant.xtruder2.usb;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.protoplant.xtruder2.event.AnalogDataEvent;

public class AnalogModule extends UsbModule {
	
	@Inject
	public AnalogModule(Logger log, EventBus eb) {
		super(log, eb);
	}

	
	@Override
	protected byte[] encodePacket() {
		byte[] pkt = new byte[1];
		pkt[0]=0;
		return pkt;
	}
	
	
	@Override
	protected void decodePacket(byte[] pkt) {
        AnalogDataEvent ade = new AnalogDataEvent();
        ade.data1 = extractInt16(pkt, 3);
        ade.data2 = extractInt16(pkt, 5);
        ade.data3 = extractInt16(pkt, 7);
        ade.data4 = extractInt16(pkt, 9);
        eb.post(ade);
//        log.info(""+ade.data1);
	}
	
	
}


