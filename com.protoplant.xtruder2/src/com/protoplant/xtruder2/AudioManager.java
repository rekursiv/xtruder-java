package com.protoplant.xtruder2;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;



@Singleton
public class AudioManager {

	private Logger log;
	
	@Inject
	public AudioManager(Logger log) {
		this.log = log;
	}

	public void playClip(String clipName) {
		String filePath = System.getProperty("user.dir")+"/audio/"+clipName+".wav";
		try {
			if (System.getProperty("os.name").startsWith("Linux")) {
				Runtime.getRuntime().exec("aplay "+filePath);
			} else {
				Runtime.getRuntime().exec("powershell -c (New-Object Media.SoundPlayer "+filePath.replace("/", "\\")+").PlaySync();");
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "", e);
		}
	}
	
}
