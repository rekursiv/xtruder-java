package com.protoplant.xtruder2;

import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;

public class StepperModule extends UsbModule {

	
	private volatile int curSpeed = 0;
	
	@Inject
	public StepperModule(Logger log, EventBus eb) {
		super(log, eb);
	}
	
	@Subscribe
	public void onSpeedChange(StepperSpeedChangeEvent evt) {
		if (evt.getSerial().compareTo(devInfo.getSerial_number())==0) {
			curSpeed = evt.getSpeed();
		}
	}

	
	@Override
	protected byte[] encodePacket() {
		byte[] pkt = new byte[3];
		pkt[0]=1;
		pkt[1]=(byte)((curSpeed>>8)&0xFF);
		pkt[2]=(byte)(curSpeed&0xFF);
		return pkt;
	}
		
	@Override
	protected void decodePacket(byte[] pkt) {
        eb.post(new StepperStatusEvent(devInfo.getSerial_number(), extractInt16(pkt, 3), pkt[5]&0xFF, pkt[6]&0xFF));

//        log.info(devInfo.getSerial_number()+":"+extractInt16(pkt, 3)+":"+(pkt[5]&0xFF)+":"+(pkt[6]&0xFF));
        
//        log.info(DatatypeConverter.printHexBinary(pkt));
 //       log.info(pkt[1]+":"+pkt[2]+"     "+extractInt16(pkt, 3)+"    "+(int)(pkt[5]&0xFF)+"    "+toBinary((int)(pkt[6]&0xFF)) );
		
	}
	

}


