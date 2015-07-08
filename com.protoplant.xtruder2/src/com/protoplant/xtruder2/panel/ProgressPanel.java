package com.protoplant.xtruder2.panel;

import java.util.logging.Logger;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.nebula.visualization.widgets.figures.ProgressBarFigure;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.CoilMassEvent;
import com.protoplant.xtruder2.event.CoilResetEvent;
import com.protoplant.xtruder2.event.SpoolTargetMassEvent;
import com.protoplant.xtruder2.event.CoilResetEvent.Context;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;

public class ProgressPanel extends Composite {

	private Logger log;
	private ProgressBarFigure progBar;
	private EventBus eb;
	private int spoolTargetMass;

	public ProgressPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Canvas canvas = new Canvas(this, SWT.NONE);
		final LightweightSystem lws = new LightweightSystem(canvas);
		
		progBar = new ProgressBarFigure();
		
		progBar.setHorizontal(true);
		progBar.setShowHi(false);
		progBar.setShowHihi(false);
		progBar.setShowLo(false);
		progBar.setShowLolo(false);
		progBar.setShowScale(false);
		progBar.setShowMarkers(false);
		progBar.setShowMinorTicks(false);

		progBar.setFont(XYGraphMediaFactory.getInstance().getFont("serif", 50, 0));
		progBar.setValue(0);
		progBar.setFillColor(XYGraphMediaFactory.getInstance().getColor(0, 255, 0));
		
		progBar.setValueLabelFormat("0.00");

		progBar.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClicked(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				reset();
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		
		lws.setContents(progBar);
		
		if (injector!=null) injector.injectMembers(this);
	}
	
	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
	}
	
	@Subscribe
	public void onCoilMass(CoilMassEvent e) {
		float mass = e.getMass();
		progBar.setValue(mass);
		if (mass>spoolTargetMass-10) {
			progBar.setFillColor(XYGraphMediaFactory.getInstance().getColor(255, 0, 0));
		} else if (mass>spoolTargetMass-50) {
			progBar.setFillColor(XYGraphMediaFactory.getInstance().getColor(255, 255, 0));
		} else {
			progBar.setFillColor(XYGraphMediaFactory.getInstance().getColor(0, 255, 0));
		}
	}
	
	@Subscribe
	public void onSpoolTargetMass(SpoolTargetMassEvent evt) {
		spoolTargetMass=evt.getMass();
		progBar.setRange(0, spoolTargetMass);
	}
	
	private void reset() {
		eb.post(new CoilResetEvent(Context.RESET));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
