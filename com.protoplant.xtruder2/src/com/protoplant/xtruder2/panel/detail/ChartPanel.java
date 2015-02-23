package com.protoplant.xtruder2.panel.detail;


import java.util.logging.Logger;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.ErrorBarType;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.SWTResourceManager;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.XtruderConfig;
import com.protoplant.xtruder2.event.AnalogDataEvent;
import com.protoplant.xtruder2.event.IndicatorDataEvent;
import com.protoplant.xtruder2.event.StepperStatusEvent;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class ChartPanel extends Composite {

	private Logger log;
	private EventBus eb;
	private XtruderConfig config;	
	
	private XYGraph graph = new XYGraph();
	private CircularBufferDataProvider diameterData = new CircularBufferDataProvider(true);
	private CircularBufferDataProvider pressureData = new CircularBufferDataProvider(true);
	private CircularBufferDataProvider velocityData = new CircularBufferDataProvider(true);
	
	private float prevDiaMin=0;
	private float prevDiaMax=0;


	public ChartPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setLayout(new FormLayout());
		
		Canvas canvas = new Canvas(this, SWT.NONE);
		FormData fd_canvas = new FormData();
		fd_canvas.bottom = new FormAttachment(100, -55);
		fd_canvas.right = new FormAttachment(100, -12);
		fd_canvas.top = new FormAttachment(0, 12);
		fd_canvas.left = new FormAttachment(0, 12);
		canvas.setLayoutData(fd_canvas);
//		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		final LightweightSystem lws = new LightweightSystem(canvas);	
		
		Button btnReset = new Button(this, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				reset();
			}
		});
		FormData fd_btnReset = new FormData();
		fd_btnReset.bottom = new FormAttachment(100, -12);
		fd_btnReset.right = new FormAttachment(0, 105);
		fd_btnReset.top = new FormAttachment(100, -46);
		fd_btnReset.left = new FormAttachment(0, 15);
		btnReset.setLayoutData(fd_btnReset);
		btnReset.setText("Reset");
		lws.setContents(graph);

		
		setBufferSizes(1000);
		
		graph.primaryXAxis.setDateEnabled(true);
		graph.primaryXAxis.setAutoScale(true);
		graph.primaryXAxis.setTitle("Time");
		graph.primaryXAxis.setShowMajorGrid(true);

		graph.primaryYAxis.setEnabled(false);
		graph.primaryYAxis.setVisible(false);
		
		setupDiameterTrace();
		setupPressureTrace();
		setupVelocityTrace();
		
		
		if (injector!=null) injector.injectMembers(this);

	}
	
	private void reset() {
		diameterData.clearTrace();
		pressureData.clearTrace();
		velocityData.clearTrace();
	}
	
	private void setBufferSizes(int bufSize) {
		diameterData.setBufferSize(bufSize);
		pressureData.setBufferSize(bufSize);
		velocityData.setBufferSize(bufSize);		
	}
	
	@Inject
	public void inject(Logger log, EventBus eb, XtruderConfig config) {
		this.log = log;
		this.eb = eb;
		this.config = config;
	}
	
	@Subscribe
	public void onInidcatorData(final IndicatorDataEvent evt) {
		float cur = evt.getCur();
		float max = evt.getMax();
		float min = evt.getMin();
		float plus = 0;
		float minus = 0;
		if (max>prevDiaMax) plus=max-cur;
		if (min<prevDiaMin) minus=cur-min;
		prevDiaMax=max;
		prevDiaMin=min;
		diameterData.addSample(new Sample(System.currentTimeMillis(), cur, plus, minus, 0, 0));
	}
	
	@Subscribe
	public void onAnalogData(final AnalogDataEvent evt) {
		pressureData.addSample(new Sample(System.currentTimeMillis(), evt.data1*0.3f));
	}
	
	@Subscribe
	public void onStepperStatus(final StepperStatusEvent evt) {
		if (evt.getFunction()==StepperFunction.TopRoller) {
			int speed = Math.abs(evt.getSpeed());
			int microStepsPerRev = config.conveyance.stepsPerRev*config.conveyance.microStepsPerStep;
			float rollerCircumference = config.conveyance.rollerDiameter*(float)Math.PI;
			float ips = ((float)speed/(float)microStepsPerRev)*rollerCircumference;
			velocityData.addSample(new Sample(System.currentTimeMillis(), ips));
		}
	}
	
	
	private Color getDiameterColor() {
		return XYGraphMediaFactory.getInstance().getColor(0, 0, 190);
	}
	
	private Color getPressureColor() {
		return XYGraphMediaFactory.getInstance().getColor(190, 0, 0);
	}
	
	private Color getVelocityColor() {
		return XYGraphMediaFactory.getInstance().getColor(0, 190, 0);
	}

	private void setupDiameterTrace() {
		Axis diameterAxis = new Axis("Diameter, mm", true);
		diameterAxis.setAutoScale(true);  //setRange(1.5, 1.9);
		diameterAxis.setForegroundColor(getDiameterColor());
		graph.addAxis(diameterAxis);
		
		Trace diameterTrace = new Trace("Diameter", graph.primaryXAxis, diameterAxis, diameterData);
		diameterTrace.setTraceColor(getDiameterColor());
		diameterTrace.setErrorBarEnabled(true);
		diameterTrace.setYErrorBarType(ErrorBarType.BOTH);
		diameterTrace.setXErrorBarType(ErrorBarType.NONE);
		diameterTrace.setErrorBarCapWidth(0);
		diameterTrace.setLineWidth(2);

		graph.addTrace(diameterTrace);
	}
	
	private void setupPressureTrace() {
		Axis pressureAxis = new Axis("Pressure, psi", true);
		pressureAxis.setAutoScale(true);  //setRange(500, 2000);
		pressureAxis.setForegroundColor(getPressureColor());
		pressureAxis.setPrimarySide(false);
		graph.addAxis(pressureAxis);
		
		Trace pressureTrace = new Trace("Pressure", graph.primaryXAxis, pressureAxis, pressureData);
		pressureTrace.setTraceColor(getPressureColor());
		graph.addTrace(pressureTrace);
	}
	
	private void setupVelocityTrace() {
		Axis velocityAxis = new Axis("Velocity, in/sec", true);
		velocityAxis.setForegroundColor(getVelocityColor());
		velocityAxis.setAutoScale(true);    //setRange(5, 12);
		graph.addAxis(velocityAxis);
		
		Trace velocityTrace = new Trace("Velocity", graph.primaryXAxis, velocityAxis, velocityData);
		velocityTrace.setTraceColor(getVelocityColor());
		graph.addTrace(velocityTrace);

	}
	

	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}