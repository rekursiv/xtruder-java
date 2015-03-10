package com.protoplant.xtruder2.panel.detail;


import java.util.logging.Logger;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.ZoomType;
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
import com.protoplant.xtruder2.ConversionManager;
import com.protoplant.xtruder2.SWTResourceManager;
import com.protoplant.xtruder2.StepperFunction;
import com.protoplant.xtruder2.config.XtruderConfig;
import com.protoplant.xtruder2.event.AnalogDataEvent;
import com.protoplant.xtruder2.event.CoilResetEvent;
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
import org.eclipse.swt.widgets.Group;


public class ChartPanel extends Composite {

	private Logger log;
	private EventBus eb;
//	private XtruderConfig config;	
	
	private XYGraph graph = new XYGraph();
	private CircularBufferDataProvider diameterData = new CircularBufferDataProvider(true);
	private CircularBufferDataProvider pressureData = new CircularBufferDataProvider(true);
	private CircularBufferDataProvider velocityData = new CircularBufferDataProvider(true);
	
	private float samplePrevDiaMin=0;
	private float samplePrevDiaMax=0;
	private ConversionManager convert;

	private float curDiaMin=0;
	private float curDiaMax=0;
//	private float prevDiaMin=0;
//	private float prevDiaMax=0;
	private Label lblPrevMax;
	private Label lblPrevMin;
	private Label lblMax;
	private Label lblMin;
	private Button btnResetDia;
	
	
	
	
	public ChartPanel(Composite parent, Injector injector) {
		super(parent, SWT.NONE);
		setLayout(new FormLayout());
		
		Canvas canvas = new Canvas(this, SWT.NONE);
		FormData fd_canvas = new FormData();
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
		fd_btnReset.right = new FormAttachment(0, 105);
		fd_btnReset.left = new FormAttachment(0, 15);
		btnReset.setLayoutData(fd_btnReset);
		btnReset.setText("Reset Chart");
		
		Button btnTest = new Button(this, SWT.NONE);
		fd_btnReset.top = new FormAttachment(100, -55);
		fd_btnReset.bottom = new FormAttachment(100, -21);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				test();
			}
		});
		FormData fd_btnTest = new FormData();
		fd_btnTest.bottom = new FormAttachment(0, 844);
		fd_btnTest.top = new FormAttachment(0, 810);
		fd_btnTest.right = new FormAttachment(0, 600);
		fd_btnTest.left = new FormAttachment(0, 525);
		btnTest.setLayoutData(fd_btnTest);
		btnTest.setText("TEST");
		
		Group grpDiameterMinmax = new Group(this, SWT.NONE);
		fd_canvas.bottom = new FormAttachment(100, -105);
		grpDiameterMinmax.setText("Diameter Min/Max");
		FormData fd_grpDiameterMinmax = new FormData();
		fd_grpDiameterMinmax.top = new FormAttachment(100, -99);
		fd_grpDiameterMinmax.bottom = new FormAttachment(100, -12);
		fd_grpDiameterMinmax.right = new FormAttachment(0, 465);
		fd_grpDiameterMinmax.left = new FormAttachment(0, 130);
		grpDiameterMinmax.setLayoutData(fd_grpDiameterMinmax);
		
		lblPrevMax = new Label(grpDiameterMinmax, SWT.BORDER);
		lblPrevMax.setBounds(10, 20, 100, 25);
		
		lblPrevMin = new Label(grpDiameterMinmax, SWT.BORDER);
		lblPrevMin.setBounds(10, 51, 100, 25);
		
		lblMax = new Label(grpDiameterMinmax, SWT.BORDER);
		lblMax.setBounds(130, 20, 100, 25);
		
		lblMin = new Label(grpDiameterMinmax, SWT.BORDER);
		lblMin.setBounds(130, 51, 100, 25);
		
		btnResetDia = new Button(grpDiameterMinmax, SWT.NONE);
		btnResetDia.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				resetDia();
			}
		});
		btnResetDia.setBounds(236, 20, 87, 35);
		btnResetDia.setText("Reset Dia");
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
	
	@Subscribe
	public void onCoilReset(CoilResetEvent event) {
		resetDia();
	}
	
	
	protected void resetDia() {
		lblPrevMin.setText(lblMin.getText());
		lblPrevMax.setText(lblMax.getText());
		curDiaMin=0;
		curDiaMax=0;
		lblMin.setText(String.format("%.3f", curDiaMin));
		lblMax.setText(String.format("%.3f", curDiaMax));
	}

	protected void test() {
//		graph.setZoomType(ZoomType.PANNING);
//		Dimension size = graph.getPlotArea().getSize();
//		graph.getPlotArea().zoomInOut(true, false, size.width, size.height/2, 0.1);
		
//		graph.setZoomType(ZoomType.NONE);
		
//		long present = System.currentTimeMillis();
//		long past = present-1000;
//		graph.primaryXAxis.setRange(past, present);

		
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
	public void inject(Logger log, EventBus eb, ConversionManager convert) {
		this.log = log;
		this.eb = eb;
		this.convert = convert;
	}
	
	@Subscribe
	public void onInidcatorData(final IndicatorDataEvent evt) {
		float cur = evt.getCur();
		float max = evt.getMax();
		float min = evt.getMin();
		float plus = 0;
		float minus = 0;
		if (max>samplePrevDiaMax) plus=max-cur;
		if (min<samplePrevDiaMin) minus=cur-min;
		samplePrevDiaMax=max;
		samplePrevDiaMin=min;
		diameterData.addSample(new Sample(System.currentTimeMillis(), cur, plus, minus, 0, 0));
		
		if (max>curDiaMax) {
			curDiaMax=max;
			lblMax.setText(String.format("%.3f", max));
		}
		if (curDiaMin<0.1||min<curDiaMin) {
			curDiaMin=min;
			lblMin.setText(String.format("%.3f", min));
		}
		
	}
	
	@Subscribe
	public void onAnalogData(final AnalogDataEvent evt) {
		pressureData.addSample(new Sample(System.currentTimeMillis(), convert.toPsi(evt.getPressure())));
	}
	
	@Subscribe
	public void onStepperStatus(final StepperStatusEvent evt) {
		if (evt.getFunction()==StepperFunction.TopRoller) {
			velocityData.addSample(new Sample(System.currentTimeMillis(), convert.toIps(evt.getSpeed())));
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
//		diameterAxis.setAutoScale(true);  //setRange(1.5, 1.9);
		diameterAxis.setRange(0, 4.0);
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
//		pressureAxis.setAutoScale(true);
		pressureAxis.setRange(0, 3200);
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
//		velocityAxis.setAutoScale(true);
		velocityAxis.setRange(0, 30);
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
