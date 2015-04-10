package com.protoplant.xtruder2.usb;

import java.util.logging.Logger;

import org.eclipse.swt.widgets.Display;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.StepperConfig;
import com.protoplant.xtruder2.config.StepperConfigManager;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.StepperResetEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;
import com.protoplant.xtruder2.event.StepperStopEvent;
import com.protoplant.xtruder2.event.StepperUpdateEvent;

public class StepperModule extends UsbModule {

	enum CommandType {PING, SET_CONFIG, SET_SPEED, CLEAR_STATUS};
	private CommandType curCmd = CommandType.PING;
	private volatile int curSpeed = 0;
	private volatile int runSpeed = 0;
	private boolean isRunning = false;    ///////////////    TEST    for "rolling update"  = true
	private StepperConfigManager scm;
	private StepperFunction function = StepperFunction.UNDEFINED;

	@Inject
	public StepperModule(Logger log, EventBus eb, StepperConfigManager scm) {
		super(log, eb);
		this.scm = scm;
	}

	@Subscribe
	public synchronized void onSpeedChange(StepperSpeedChangeEvent evt) {
		if (evt.getFunction() == function) {
			runSpeed = evt.getSpeed();
			if (isRunning) {
				setSpeed(runSpeed);
			}
		}
	}

	@Subscribe
	public synchronized void onConfigSetup(ConfigSetupEvent evt) {
		update();
	}

	@Subscribe
	public synchronized void onReset(StepperResetEvent evt) {   
		if (evt.getFunction() == function) {
			curCmd = CommandType.CLEAR_STATUS;
		}
	}
	
	@Subscribe
	public synchronized void onRun(StepperRunEvent evt) {
		if (evt.getFunction() == function) {
			setSpeed(runSpeed);
			isRunning = true;
		}
	}
	
	@Subscribe
	public synchronized void onStop(StepperStopEvent evt) {
		if (evt.getFunction() == function) {
			setSpeed(0);
			isRunning = false;
		}
	}

/*	
 * ///////////////    disabled    for "rolling update"
	@Override
	public synchronized void release() {
		setSpeed(0);
		refreshWrite();
		super.release();
	}
*/
	
	@Override
	public synchronized void connect(HIDDeviceInfo devInfo) {
		super.connect(devInfo);
		update();
	}
	
	@Override
	public synchronized void disconnect() {
		if (isConnected()) {
			super.disconnect();
			update();
		}
	}

	@Override
	protected synchronized byte[] encodePacket() {
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
	protected synchronized void decodePacket(byte[] pkt) {
		StepperStatusEvent ssEvent = new StepperStatusEvent(function, extractInt16(pkt, 3), pkt[5]&0xFF, pkt[6]&0xFF, extractInt16(pkt, 7));
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				eb.post(ssEvent);
			}
		});
	}

	private void update() {
		String serial = null;
		if (devInfo!=null) serial = devInfo.getSerial_number();
		StepperFunction newFunction = scm.getFunction(serial);
		if (isConnected()) {
			curCmd = CommandType.SET_CONFIG;
			postUpdateToUiThread(new StepperUpdateEvent(newFunction, serial));
			if (function==StepperFunction.UNDEFINED) log.info("Stepper "+newFunction.name()+" connected.");   //   FIXME  fires when updated with undefined stepper
//			else log.info("Stepper "+newFunction.name()+" updated.");
		} else {
			postUpdateToUiThread(new StepperUpdateEvent(function, null));
			log.info("Stepper "+function.name()+" disconnected.");
		}
		function = newFunction;
	}
	
	private void postUpdateToUiThread(StepperUpdateEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				eb.post(event);
			}
		});
	}
	
	private void setSpeed(int speed) {
		curSpeed=speed;
		if (curCmd!=CommandType.SET_CONFIG) curCmd = CommandType.SET_SPEED;
	}
	
	
	
	
	
	private void OLD_update() {
		String serial = null;
		if (devInfo!=null) serial = devInfo.getSerial_number();
		StepperFunction newFunction = scm.getFunction(serial);
		if (!isConnected()||newFunction==StepperFunction.UNDEFINED) {
			postUpdateToUiThread(new StepperUpdateEvent(function, null));
			log.info("Stepper "+function.name()+" disconnected.");
		} else {
			curCmd = CommandType.SET_CONFIG;
			postUpdateToUiThread(new StepperUpdateEvent(newFunction, serial));
			if (function==StepperFunction.UNDEFINED) log.info("Stepper "+newFunction.name()+" connected.");
		}
		function = newFunction;
	}
	
}


