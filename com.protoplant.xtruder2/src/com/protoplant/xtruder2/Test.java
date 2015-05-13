package com.protoplant.xtruder2;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;



@Singleton
public class Test {

	private Logger log;
	private AudioManager am;
	
	@Inject
	public Test(Logger log, AudioManager am) {
		this.log = log;
		this.am = am;
	}

	public void test() {
		log.info(System.getProperty("os.name"));
		am.playClip("mark");
	}
	
}
