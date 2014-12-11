package com.protoplant.xtruder2;

import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;

public class StepperModule extends UsbModule {

	enum CommandType {PING, SET_CONFIG, SET_SPEED, CLEAR_STATUS};
	private CommandType curCmd = CommandType.PING;
	private volatile int curSpeed = 0;
	private StepperConfigManager scm;
	private StepperType type = StepperType.UNDEFINED;

	@Inject
	public StepperModule(Logger log, EventBus eb, StepperConfigManager scm) {
		super(log, eb);
		this.scm = scm;
	}

	@Subscribe
	public void onSpeedChange(StepperSpeedChangeEvent evt) {
		if (evt.getType() == type) {
			curSpeed = evt.getSpeed();
			curCmd = CommandType.SET_SPEED;
		}
	}

	@Override
	public void connect(HIDDeviceInfo devInfo) {
		super.connect(devInfo);
		if (this.devInfo!=null) {
			type = scm.getType(this.devInfo);
		}
	}

	@Override
	protected byte[] encodePacket() {
		byte[] pkt;
		switch (curCmd) {
			case SET_CONFIG:
				pkt = new byte[9];
				pkt[0]=1;
			break;
			case SET_SPEED:
				pkt = new byte[3];
				pkt[0]=2;
				pkt[1]=(byte)((curSpeed>>8)&0xFF);
				pkt[2]=(byte)(curSpeed&0xFF);
			break;
			case CLEAR_STATUS:
				pkt = new byte[1];
				pkt[0]=3;
			break;
			default:
				pkt = new byte[1];
				pkt[0]=0;
			break;
		}
		curCmd = CommandType.PING;
		return pkt;
	}

	@Override
	protected void decodePacket(byte[] pkt) {
        eb.post(new StepperStatusEvent(type, extractInt16(pkt, 3), pkt[5]&0xFF, pkt[6]&0xFF));

//        log.info(devInfo.getSerial_number()+":"+extractInt16(pkt, 3)+":"+(pkt[5]&0xFF)+":"+(pkt[6]&0xFF));
        
//        log.info(DatatypeConverter.printHexBinary(pkt));
 //       log.info(pkt[1]+":"+pkt[2]+"     "+extractInt16(pkt, 3)+"    "+(int)(pkt[5]&0xFF)+"    "+toBinary((int)(pkt[6]&0xFF)) );
		
	}


}


