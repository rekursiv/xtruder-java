package com.protoplant.xtruder2;

import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class AudioClip {

	private static Logger log = Logger.getLogger(AudioClip.class.getName());
	private Clip clip = null;
	private String fileName = null;
	
	public AudioClip(String fileName) {
		this.fileName = fileName;
		try {
			load();
		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
		}
	}
	
	public void load() throws Exception {
		if (clip==null) clip = AudioSystem.getClip();
		String dir = System.getProperty("user.dir")+"/audio/";
        URL url = Paths.get(dir+fileName).toUri().toURL();
//        log.info("Loading audio clip:  "+url.toString());
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        clip.open(audioIn);
        audioIn.close();
	}

	public void play() {
		if (clip==null || !clip.isOpen()) {
			log.warning("Clip '"+fileName+" not ready.");
			return;
		}
		if (clip.isRunning()) return;
		clip.setFramePosition(0);
		clip.start();
	}

	public void close() {
		if (clip!=null && clip.isOpen()) clip.close();
	}
	
}
