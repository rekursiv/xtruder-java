package com.protoplant.xtruder2.usb;

import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.StepperConfig;
import com.protoplant.xtruder2.config.StepperConfigManager;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.StepperDisconnectEvent;
import com.protoplant.xtruder2.event.StepperResetEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;
import com.protoplant.xtruder2.event.StepperStopEvent;
import com.protoplant.xtruder2.event.StepperConnectEvent;

public class StepperModule extends UsbModule {

	enum CommandType {PING, SET_CONFIG, SET_SPEED, CLEAR_STATUS};
	private CommandType curCmd = CommandType.PING;
	private volatile int curSpeed = 0;
	private volatile int runSpeed = 0;
	private boolean isRunning = false;
	private StepperConfigManager scm;
	private StepperFunction function = StepperFunction.UNDEFINED;

	@Inject
	public StepperModule(Logger log, EventBus eb, StepperConfigManager scm) {
		super(log, eb);
		this.scm = scm;
	}

	@Subscribe
	public void onSpeedChange(StepperSpeedChangeEvent evt) {
		if (evt.getFunction() == function) {
			runSpeed = evt.getSpeed();
			if (isRunning) {
				curSpeed = runSpeed;
				curCmd = CommandType.SET_SPEED;
			}
		}
	}

	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		if (devInfo!=null) {
			function = scm.getFunction(devInfo);
			if (function!=StepperFunction.UNDEFINED) {
				curCmd = CommandType.SET_CONFIG;
				log.info("SET_CONFIG");
			}
		}
	}
	
	@Subscribe
	public void onReset(StepperResetEvent evt) {   
		if (evt.getFunction() == function) {
			curCmd = CommandType.CLEAR_STATUS;
		}
	}
	
	@Subscribe
	public void onRun(StepperRunEvent evt) {
		if (evt.getFunction() == function) {
			curSpeed = runSpeed;
			curCmd = CommandType.SET_SPEED;
			isRunning = true;
		}
	}
	
	@Subscribe
	public void onStop(StepperStopEvent evt) {
		if (evt.getFunction() == function) {
			curSpeed = 0;
			curCmd = CommandType.SET_SPEED;
			isRunning = false;
		}
	}
	
	@Override
	public void connect(HIDDeviceInfo devInfo) {
		super.connect(devInfo);
		if (this.devInfo!=null) {
			function = scm.getFunction(this.devInfo);
			eb.post(new StepperConnectEvent(function, this.devInfo.getSerial_number()));
			curCmd = CommandType.SET_CONFIG;
			log.info("Stepper "+function.name()+" connected.");
		}
	}
	
	@Override
	public void release() {
		curSpeed = 0;
		curCmd = CommandType.SET_SPEED;
		refreshWrite();
		super.release();
	}

	@Override
	public void disconnect() {
		if (isConnected()) {
			eb.post(new StepperDisconnectEvent(function));
			log.info("Stepper "+function.name()+" disconnected.");
		}
		super.disconnect();
	}

	@Override
	protected byte[] encodePacket() {
		byte[] pkt;
		switch (curCmd) {
			case SET_CONFIG:
				StepperConfig sc = scm.getConfig(function);
				if (sc==null) {
					pkt = new byte[1];
					pkt[0]=0;
					curCmd = CommandType.PING;
				} else {
					pkt = new byte[15];
					pkt[0]=1;
					pkt[1]=(byte)(sc.stepMode&0xFF);
					pkt[2]=(byte)(sc.isGain&0xFF);
					pkt[3]=(byte)(sc.holdingTorque&0xFF);
					pkt[4]=(byte)(sc.minTorque&0xFF);
					pkt[5]=(byte)(sc.maxTorque&0xFF);
					pkt[6]=(byte)(sc.torqueDiv&0xFF);
					pkt[7]=(byte)(sc.accelDiv&0xFF);
					pkt[8]=(byte)(sc.accelStep&0xFF);
					
					pkt[9]=(byte)((sc.loPos>>8)&0xFF);
					pkt[10]=(byte)(sc.loPos&0xFF);
					pkt[11]=(byte)((sc.hiPos>>8)&0xFF);
					pkt[12]=(byte)(sc.hiPos&0xFF);
					pkt[13]=(byte)((sc.posCountDiv>>8)&0xFF);
					pkt[14]=(byte)(sc.posCountDiv&0xFF);
					
					curCmd = CommandType.SET_SPEED;
//					log.info("CONFIG:  "+(sc.maxTorque&0xFF));
				}
			break;
			case SET_SPEED:
				pkt = new byte[3];
				pkt[0]=2;
				pkt[1]=(byte)((curSpeed>>8)&0xFF);
				pkt[2]=(byte)(curSpeed&0xFF);
				curCmd = CommandType.PING;
//				log.info("SPEED:  "+curSpeed);
			break;
			case CLEAR_STATUS:
				pkt = new byte[1];
				pkt[0]=3;
				curCmd = CommandType.PING;
			break;
			default:
				pkt = new byte[1];
				pkt[0]=0;
				curCmd = CommandType.PING;
			break;
		}
		return pkt;
	}

	@Override
	protected void decodePacket(byte[] pkt) {
        eb.post(new StepperStatusEvent(function, extractInt16(pkt, 3), pkt[5]&0xFF, pkt[6]&0xFF, extractInt16(pkt, 7)));

//        log.info("steps="+extractInt16(pkt, 7));
        
//        log.info(devInfo.getSerial_number()+":"+extractInt16(pkt, 3)+":"+(pkt[5]&0xFF)+":"+(pkt[6]&0xFF));
        
//        log.info(DatatypeConverter.printHexBinary(pkt));
 //       log.info(pkt[1]+":"+pkt[2]+"     "+extractInt16(pkt, 3)+"    "+(int)(pkt[5]&0xFF)+"    "+toBinary((int)(pkt[6]&0xFF)) );
		
	}


}


