package com.protoplant.xtruder2.panel.detail;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.AudioManager;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.AnalogDataEvent;
import com.protoplant.xtruder2.event.StepperDisconnectEvent;
import com.protoplant.xtruder2.event.StepperRunEvent;
import com.protoplant.xtruder2.event.StepperSpeedChangeEvent;
import com.protoplant.xtruder2.panel.AdjustableStepperPanel;
import com.protoplant.xtruder2.panel.TrackingStepperPanel;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class TestDetailPanel extends Composite {

	private Logger log;
	private EventBus eb;
	private XtruderConfig config;
	
	protected Button btnTest;
	
	protected AdjustableStepperPanel asp;
	protected TrackingStepperPanel tsp;
	protected Composite composite;
	protected Label lblTest;
	private Text txtTheHopperIs;
	private volatile boolean isTalking = false;
	private AudioManager am;
	private int count;


	public TestDetailPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		
		
		btnTest = new Button(this, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
//				lblTest.setText("testing...");
				test();
			}
		});
		btnTest.setBounds(20, 229, 75, 25);
		btnTest.setText("TEST");
		
		lblTest = new Label(this, SWT.NONE);
		lblTest.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblTest.setBounds(20, 108, 569, 45);
		
		txtTheHopperIs = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtTheHopperIs.setText("50 grams to go");
		txtTheHopperIs.setBounds(10, 10, 616, 56);
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, AudioManager am) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.am = am;
	}

	
	@Subscribe
	public void onAnalogData(final AnalogDataEvent evt) {
		lblTest.setText(""+evt.getPressure());
	}
	
	
	public void test() {
		
	}
	
	public void test_mass() {
		eb.post(new StepperSpeedChangeEvent(StepperFunction.TopRoller, 4000));
		eb.post(new StepperRunEvent(StepperFunction.TopRoller));
	}
	
	
	public void test_audio() {
		am.speak(txtTheHopperIs.getText());
//		am.listVoices();
//		listAllVoices();
/*		
	     if (!isTalking) {
	 		final String txt = txtTheHopperIs.getText();
			final boolean is16bit = btnBit16.getSelection();
		    Runnable r = new Runnable() {
		         public void run() {
		     		speak(txt, is16bit);
		         }
		     };
	    	 new Thread(r).start();
	     }
*/
	}
	
	
	public void speak(String text, boolean is16bit) {
		isTalking = true;
		String voiceName = "kevin";
		if (is16bit) voiceName = "kevin16";
		
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice helloVoice = voiceManager.getVoice(voiceName);

		if (helloVoice == null) {
			log.warning("Cannot find a voice named " + voiceName + ".  Please specify a different voice.");
			return;
		}

		helloVoice.allocate();
		helloVoice.speak(text);
		helloVoice.deallocate();
		isTalking = false;
	}
	
	public void listAllVoices() {
		System.out.println();
		System.out.println("All voices available:");
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice[] voices = voiceManager.getVoices();
		
		for (int i = 0; i < voices.length; i++) {
			System.out.println("    " + voices[i].getName() + " (" + voices[i].getDomain() + " domain)");
		}
		
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
