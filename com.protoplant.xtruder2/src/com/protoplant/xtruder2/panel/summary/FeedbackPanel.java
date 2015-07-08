package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.MachineState;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.ConfigSetupEvent;
import com.protoplant.xtruder2.event.ConfigStoreEvent;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.ProductionModeChangeEvent;
import com.protoplant.xtruder2.event.StepperSpeedNudgeEvent;
import com.protoplant.xtruder2.panel.summary.ProductionPanel.Mode;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class FeedbackPanel extends Group {

	private Logger log;
	private EventBus eb;
	private XtruderConfig config;
	
	private volatile int fbCenterCount=0;
	private Label lblDbCount;
	private Label lblDelta;
	private Button chbEnabled;
	private Spinner spnTargetDia;
	private int timerCount;
	private MachineState ms;
	

	public FeedbackPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		setText("Feedback");
		
		chbEnabled = new Button(this, SWT.CHECK);
		chbEnabled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!chbEnabled.getSelection()) {
					lblDelta.setText("-");
					lblDbCount.setText("-");
				}
			}
		});
		chbEnabled.setBounds(88, 10, 93, 16);
		chbEnabled.setText("Enabled");
		
		Label lblDbCountTitle = new Label(this, SWT.NONE);
		lblDbCountTitle.setBounds(10, 31, 66, 23);
		lblDbCountTitle.setText("DBCount:");
		
		lblDbCount = new Label(this, SWT.NONE);
		lblDbCount.setText("-");
		lblDbCount.setBounds(88, 31, 50, 22);
		
		Label lblDeltaTitle = new Label(this, SWT.NONE);
		lblDeltaTitle.setText("Delta:");
		lblDeltaTitle.setBounds(164, 31, 44, 23);
		
		lblDelta = new Label(this, SWT.NONE);
		lblDelta.setText("-");
		lblDelta.setBounds(227, 32, 56, 22);
		
		spnTargetDia = new Spinner(this, SWT.BORDER);
		spnTargetDia.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				config.feedback.targetDiameter=(float)spnTargetDia.getSelection()/100.0f;
			}
		});
		spnTargetDia.setBounds(136, 64, 135, 36);
		spnTargetDia.setMaximum(300);
		spnTargetDia.setMinimum(150);
		spnTargetDia.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config, MachineState ms) {
		this.log = log;
		this.eb = eb;
		this.config = config;
		this.ms = ms;
	}

	@Subscribe
	public void onConfigSetup(ConfigSetupEvent evt) {
		spnTargetDia.setSelection((int)(config.feedback.targetDiameter*100));
	}

	@Subscribe
	public void onConfigStore(ConfigStoreEvent evt) {
		ms.feedback.targetDiameter = config.feedback.targetDiameter;
	}
	
	@Subscribe
	public void onInidcatorData(final IndicatorDataEvent evt) {
		if (timerCount>config.feedback.period) {
			timerCount=0;
			if (chbEnabled.getSelection()) doFeedback(evt.getCur());
		} else {
			++timerCount;
		}
		
	}
	
	@Subscribe
	public void onProductionModeChange(final ProductionModeChangeEvent evt) {
		if (evt.getMode()==Mode.EXTRUDE) {
			chbEnabled.setSelection(true);
		} else {
			chbEnabled.setSelection(false);
			lblDelta.setText("-");
			lblDbCount.setText("-");
		}
	}
	
	
	
	public void doFeedback(float diameter) {
		float delta = diameter-config.feedback.targetDiameter;
		if (Math.abs(delta)<config.feedback.deadband) {
			++fbCenterCount;
		} else {
			fbCenterCount=0;
			if (delta>config.feedback.spread) delta=config.feedback.spread;
			else if (delta<-config.feedback.spread) delta=-config.feedback.spread;
			delta*=config.feedback.nudgeFactor;
			eb.post(new StepperSpeedNudgeEvent(StepperFunction.TopRoller, (int)delta));
		}
		lblDelta.setText(""+(int)delta);
		lblDbCount.setText(""+fbCenterCount);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
