package com.protoplant.xtruder2.usb;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDDeviceNotFoundException;
import com.codeminders.hidapi.HIDManager;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;


public class UsbModule {
	protected static final int reportId = 0x3F;
	
	protected Logger log;
	protected EventBus eb;
	
	protected HIDDeviceInfo devInfo = null;
	protected HIDDevice dev = null;

	@Inject
	public UsbModule(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
	}
	
	public void release() {
		disconnect();
	}
	
	public boolean isConnected() {
		return dev!=null;
	}
	
	public void connect(HIDDeviceInfo devInfo) {
		disconnect();
        try {
			dev = HIDManager.getInstance().openById(devInfo.getVendor_id(), devInfo.getProduct_id(), devInfo.getSerial_number());
			if (dev==null) return;
			dev.disableBlocking();
			this.devInfo = devInfo;
		} catch (HIDDeviceNotFoundException e) {
			dev = null;
		} catch (IOException e) {
			dev = null;
		}
	}
	
	public void disconnect() {
		if (isConnected()) {
			try {
				dev.close();
			} catch (IOException e) {
			} finally {
				dev = null;
				devInfo = null;
			}
		}
	}
	
	public void refreshWrite() {
		if (isConnected()) {
			try {
				writeUsb();
			} catch (IOException e) {
				disconnect();
			}
		}
	}
	
	public void refreshRead() {
		if (isConnected()) {
			try {
				readUsb();
			} catch (IOException e) {
				disconnect();
			}
		}
	}
	
	protected int extractInt16(byte[] buf, int offset) {
		return (short)(((buf[offset]&0xFF)<<8)|(buf[offset+1]&0xFF));
	}
	
	protected int extractUnsignedInt16(byte[] buf, int offset) {
		return ((buf[offset]&0xFF)<<8)|(buf[offset+1]&0xFF);
	}
	
	protected String toBinary(int byteData) {
		String bits = "0000000"+Integer.toBinaryString(byteData)+" ";
		return bits.substring(bits.length() - 9, bits.length());
	}
	
	protected byte[] encodePacket() {
		byte[] pkt = new byte[1];
		pkt[0]=0;
		return pkt;
	}
	
	protected void decodePacket(byte[] pkt) {
        log.info(DatatypeConverter.printHexBinary(pkt));
	}
	
	
	private void writeUsb() throws IOException {
		byte[] pkt = encodePacket();
		byte[] data = new byte[pkt.length+2];
		data[0]=reportId;
		data[1]=(byte)pkt.length;
		for (int i=2; i<data.length; ++i) {
			data[i]=pkt[i-2];
		}
//		log.info(DatatypeConverter.printHexBinary(data));
		dev.write(data);
//		log.info("^^^");
	}
	
	private void readUsb() throws IOException {
        byte[] data = new byte[64];
        dev.readTimeout(data, 10);   // TODO:  check if this function returns 64 to catch errors??
//        System.out.println("pktLen="+dev.readTimeout(data, 10));
        decodePacket(data);
	}

}
