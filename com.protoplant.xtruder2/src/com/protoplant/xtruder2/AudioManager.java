package com.protoplant.xtruder2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;


@Singleton
public class AudioManager extends Thread {

	private Logger log;
	private XtruderConfig config;
	private Voice voice;
	private BlockingQueue<String> bq = new LinkedBlockingQueue<String>(10);

	
	@Inject
	public AudioManager(Logger log, XtruderConfig config) {
		this.log = log;
		this.config = config;
	}
	
	public void init() {
		System.getProperties().setProperty("mbrola.base", config.alarm.mbrolaBase);
		listVoices();
		
		voice = VoiceManager.getInstance().getVoice(config.alarm.voiceName);
		if (voice==null) {
			log.warning("Voice '"+config.alarm.voiceName+"' not available, trying default...");
			voice = VoiceManager.getInstance().getVoice("kevin16");
			if (voice==null){
				log.warning("Voice 'kevin16' not available, no voices loaded!");
				return;
			}
		}
		voice.setDurationStretch(1.1f);
		voice.allocate();
		start();
	}
	
	public void release() {
		if (voice!=null) voice.deallocate();
		interrupt();
	}
	
	@Override
	public void run() {
		while (isAlive()) {
			try {
				if (voice!=null) voice.speak(bq.take());
			} catch (InterruptedException e) {
				log.info("thread interrupt");
				return;
			}
//			System.out.println("*");
		}
	}
	
	
	public void speak(String text) {
		if (isAlive()&&voice!=null) {
			try {
				if (bq.remainingCapacity()>0) bq.put(text);
				else log.warning("Audio Manager queue is full!");
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void listVoices() {
		log.info("All voices available:");
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice[] voices = voiceManager.getVoices();
		for (int i = 0; i < voices.length; i++) {
			log.info("    " + voices[i].getName() + " (" + voices[i].getDomain() + " domain)");
		}
	}
	
	
}
