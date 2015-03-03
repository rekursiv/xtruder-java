package com.protoplant.xtruder2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;


@Singleton
public class AudioManager extends Thread {
	
	private static final String voiceName = "kevin16";
	private Logger log;
	Voice voice;
	BlockingQueue<String> bq = new LinkedBlockingQueue<String>(10);
	
	@Inject
	public AudioManager(Logger log) {
		this.log = log;
	}
	
	public void init() {
//		System.getProperties().setProperty("mbrola.base", "c:/mbrola");
//		listVoices();
		voice = VoiceManager.getInstance().getVoice(voiceName);
		voice.setDurationStretch(1.1f);
		voice.allocate();
//		System.out.println(voice.getRate());
		start();
	}
	
	public void release() {
		voice.deallocate();
		interrupt();
	}
	
	@Override
	public void run() {
		while (isAlive()) {
			try {
				voice.speak(bq.take());
			} catch (InterruptedException e) {
				log.info("thread interrupt");
				return;
			}
//			System.out.println("*");
		}
	}
	
	
	public void speak(String text) {
		try {
			if (bq.remainingCapacity()>0) bq.put(text);
			else log.warning("Audio Manager queue is full!");
		} catch (InterruptedException e) {
		}
	}
	
	public void listVoices() {
		System.out.println();
		System.out.println("All voices available:");
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice[] voices = voiceManager.getVoices();
		for (int i = 0; i < voices.length; i++) {
			System.out.println("    " + voices[i].getName() + " (" + voices[i].getDomain() + " domain)");
		}
	}
	
	
}
