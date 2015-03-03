package com.protoplant.xtruder2.usb;

import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;
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
        eb.post(new AnalogDataEvent(extractInt16(pkt, 3), extractInt16(pkt, 5), extractInt16(pkt, 7), extractInt16(pkt, 9)));
	}
	
	
}


