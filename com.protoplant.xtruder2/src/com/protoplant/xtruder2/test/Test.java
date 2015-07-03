package com.protoplant.xtruder2.test;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.protoplant.xtruder2.AudioManager;



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

	
	
	
	public void testVoices() {
		log.info("Testing voices...");
		am.playClip("50gtg");
		delay(2000);
		am.playClip("30gtg");
		delay(2000);
		am.playClip("10gtg");
		delay(2000);
		am.playClip("5");
		delay(1000);
		am.playClip("4");
		delay(1000);
		am.playClip("3");
		delay(1000);
		am.playClip("2");
		delay(1000);
		am.playClip("1");
		delay(1000);
		am.playClip("mark");
		delay(2000);
		am.playClip("dia-reset");
		delay(2000);
		am.playClip("undersize");
		delay(1000);
		am.playClip("oversize");
		delay(1000);
		am.playClip("hopper-discon");
		delay(2000);
		am.playClip("hopper-empty");
		delay(2000);
		am.playClip("pressure-high");
		log.info("Done.");
	}

	private void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
	
	
}
