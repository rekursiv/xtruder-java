package com.protoplant.xtruder2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public class FeederMonitor implements Runnable {
	private static final int port = 23;

	private Logger log;	
	private EventBus eb;
	
	private String ip = null;
	private Socket sock = null;
	private DataOutputStream os;
	private DataInputStream is;

	private volatile boolean isEmpty = false;
	private Thread thread;

	

	@Inject
	public FeederMonitor(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;   //  FIXME
	}
	
	public void connect(String ip) {
		this.ip = ip;
		thread = new Thread(this);
		thread.start();
	}
	
	public void disconnect() {
		thread.interrupt();
	}

	public boolean isEmpty() {
		return isEmpty;
	}
	
	public boolean isConnected() {
		try {
			return !sock.isClosed()&&sock.isConnected();
		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
			return false;
		}
	}

	
	@Override
	public void run() {
		if (!thread.isInterrupted()) attemptConnection();
		
		int errCount = 0;
		int bufLen = 600;
		while (!thread.isInterrupted() && isConnected() && errCount<10) {
			try {
				byte[] buf = new byte[bufLen];
				int bytesRead = is.read(buf);
				if (bytesRead>200) {
					parse(buf);
				}
			} catch (IOException e) {
//				System.out.println("!!!  "+e.getMessage());
				try {
					os.writeChar('1');
					os.writeChar('\r');
				} catch (IOException e1) {
					++errCount;
				}
				++errCount;
			}
		}
		shutdown();
	}
	
	private void shutdown() {
		try {
			sock.close();
			is.close();
			os.close();
			isEmpty = false;
			log.info("Feeder at "+ip+" disconnected.");
		} catch (IOException e) {
			log.log(Level.WARNING, "", e);
		}
	}
	
	private void attemptConnection() {
		log.info("Attempting to connect to feeder at "+ip+", port "+port+"...");
		try {
			sock = new Socket(ip, port);
			sock.setSoTimeout(1000);
			os = new DataOutputStream(sock.getOutputStream());
			is = new DataInputStream(sock.getInputStream());
			log.info("Connection established.");
		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
		}
	}
	
	private void parse(byte[] buf) {
		String msg = new String(buf);
		String[] data = msg.split("\u001b");
		if (data.length==44) {
			for (String datum : data) {
				if (datum.startsWith("[23;26f")) {
					String alarm = datum.substring(7);
//					System.out.println(alarm);
					if (alarm.equals("**")) isEmpty = false;
					else isEmpty = true;
				}
			}
		}
		
//		System.out.println(Arrays.toString(buf));
//		System.out.println(msg);		
//		System.out.println("###  "+data.length);
//		System.out.println(Arrays.toString(data));		
	}


	


}
