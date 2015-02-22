package com.protoplant.xtruder2.usb;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.protoplant.xtruder2.event.AnalogDataEvent;

public class TestModule extends UsbModule {

	int pktCount = 0;
	long cycleCount = 0;
	
	@Inject
	public TestModule(Logger log, EventBus eb) {
		super(log, eb);
	}

	
	@Override
	public void connect(HIDDeviceInfo devInfo) {
		super.connect(devInfo);
		if (this.devInfo!=null) {
			log.info(">-< CONNECT "+devInfo.getSerial_number());
		}
	}
	
	@Override
	public void disconnect() {
		if (this.devInfo!=null) {
			log.info("<  > DISCONNECT "+devInfo.getSerial_number());
		}
		super.disconnect();
	}
	
	
	@Override
	protected byte[] encodePacket() {
		byte[] pkt = new byte[1];
		pkt[0]=(byte)pktCount;
		if (pktCount>=99) {
			pktCount=0;
			++cycleCount;
		}
		else pktCount++;
		return pkt;
	}
	
	
	@Override
	protected void decodePacket(byte[] pkt) {
		if (pktCount==0) log.info(cycleCount+":"+pkt[2]+":"+devInfo.getSerial_number()+":"+DatatypeConverter.printHexBinary(pkt));    
	}
	
	
}


