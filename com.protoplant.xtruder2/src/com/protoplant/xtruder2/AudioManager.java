package com.protoplant.xtruder2;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;



@Singleton
public class AudioManager {
	
	Map<String, AudioClip> clipMap = new HashMap<String, AudioClip>();
	private Logger log;
	
	@Inject
	public AudioManager(Logger log) {
		this.log = log;
	}

	public void init() {
		addClips();
		addClip("foo");
	}
	
	public void addClips() {
		addClip("50gtg");
		addClip("30gtg");
		addClip("10gtg");
		addClip("5");
		addClip("4");
		addClip("3");
		addClip("2");
		addClip("1");
		addClip("mark");
		addClip("dia-reset");
		addClip("undersize");
		addClip("oversize");
		addClip("hopper-discon");
		addClip("hopper-empty");
		addClip("pressure-high");
	}

	public void addClip(String name) {
		AudioClip clip = new AudioClip(name+".wav");
		clipMap.put(name, clip);
	}
	
	public void release() {
		for (AudioClip clip : clipMap.values()) {
			clip.close();
		}
	}
	
	public void playClip(String clipName) {
		AudioClip clip = clipMap.get(clipName);
		if (clip!=null) clip.play();
		else log.warning("Clip not found: "+clipName);
	}

	
}
