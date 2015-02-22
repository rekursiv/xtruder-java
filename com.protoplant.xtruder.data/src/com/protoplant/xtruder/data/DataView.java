package com.protoplant.xtruder.data;


import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.CoordinateListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.BaseLine;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.ZoomType;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;


public class DataView extends Composite {

//	private final static String filePathTest = "C:/projects/Protoplant/data/test1.csv";
//	private final static String fileDialogPath = "C:/projects/Protoplant/data/";
	
	private DataReader data = new DataReader();
	private XYGraph graph = new XYGraph();
	ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(graph);
	private Axis diameterTimeAxis;
	private double curTimeOffset = 0;
	
	CircularBufferDataProvider baseLineData = new CircularBufferDataProvider(true);
	
	public DataView(Composite parent) {
		super(parent, SWT.NONE);

		graph.primaryXAxis.setDateEnabled(true);
		graph.primaryXAxis.setAutoScale(true);
		graph.primaryXAxis.setTitle("Time");
		graph.primaryXAxis.setShowMajorGrid(true);

		graph.primaryYAxis.setEnabled(false);
		graph.primaryYAxis.setVisible(false);
		
		setupDiameterTrace();
		setupPressureTrace();
		setupVelocityTrace();
		
		graph.setZoomType(ZoomType.PANNING);

		setLayout(new FillLayout(SWT.HORIZONTAL));
		Canvas canvas = new Canvas(this, SWT.NONE);
//		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		final LightweightSystem lws = new LightweightSystem(canvas);	
		lws.setContents(toolbarArmedXYGraph);
		
		Button btnLoadData = new Button("LD");
		btnLoadData.setToolTip(new Label("Load data from file..."));
		btnLoadData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
			   dialog.setFilterExtensions(new String [] {"*.csv"});
//			   dialog.setFilterPath(fileDialogPath);
			   String result = dialog.open();
			   if (result!=null) loadData(result);
			}
		});
		

		toolbarArmedXYGraph.getToolbar().addSeparator();
		toolbarArmedXYGraph.addToolbarButton(btnLoadData);
		
	
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseScrolled(MouseEvent evt) {
				graph.getPlotArea().zoomInOut(true, false, evt.x, evt.y, evt.count*0.04);
			}
		});
		
		
		diameterTimeAxis.addCoordinateListener(new CoordinateListener() {
			@Override
			public void coordinateSystemChanged(IFigure fig) {
				updateTimeOffset();
			}
		});
		

//		addTestBtn();     /////////////////   TEST BTN
//		loadData(filePathTest);   //    TEST
		

	}
	
	private void addTestBtn() {
		Button btnTest = new Button("T");
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				graph.primaryXAxis.setAutoScale(true);
//				System.out.println(graph.primaryXAxis.getRange().toString());
			}
			
		});
		toolbarArmedXYGraph.getToolbar().addSeparator();
		toolbarArmedXYGraph.addToolbarButton(btnTest);
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
		diameterTimeAxis = new Axis("", false);
		diameterTimeAxis.setDateEnabled(true);
//		diameterTimeAxis.setAutoScale(true);   /// ??
		diameterTimeAxis.setForegroundColor(getDiameterColor());
		graph.addAxis(diameterTimeAxis);
		
		Axis diameterAxis = new Axis("Diameter, mm", true);
		diameterAxis.setRange(1.5, 1.9);
		diameterAxis.setForegroundColor(getDiameterColor());
		graph.addAxis(diameterAxis);
		
		Trace diameterTrace = new Trace("Diameter", diameterTimeAxis, diameterAxis, data.diameter);
		diameterTrace.setTraceColor(getDiameterColor());

		graph.addTrace(diameterTrace);
		

		Trace baseLineTrace = new Trace("", diameterTimeAxis, diameterAxis, baseLineData);
		baseLineTrace.setTraceType(TraceType.DOT_LINE);   //  FIXME
		baseLineTrace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(200, 200, 255));
		graph.addTrace(baseLineTrace);
	}
	
	private void setupPressureTrace() {
		Axis pressureAxis = new Axis("Pressure, psi", true);
		pressureAxis.setRange(500, 2000);
		pressureAxis.setForegroundColor(getPressureColor());
		pressureAxis.setPrimarySide(false);
		graph.addAxis(pressureAxis);
		
		Trace pressureTrace = new Trace("Pressure", graph.primaryXAxis, pressureAxis, data.pressure);
		pressureTrace.setTraceColor(getPressureColor());
		graph.addTrace(pressureTrace);
	}
	
	private void setupVelocityTrace() {
		Axis velocityAxis = new Axis("Velocity, in/sec", true);
		velocityAxis.setForegroundColor(getVelocityColor());
		velocityAxis.setRange(5, 12);
		graph.addAxis(velocityAxis);
		
		Trace velocityTrace = new Trace("Velocity", graph.primaryXAxis, velocityAxis, data.velocity);
		velocityTrace.setTraceColor(getVelocityColor());
		graph.addTrace(velocityTrace);

	}
	
	private void onLoadDataSuccess() {
		
		baseLineData.clearTrace();
		if (data.diameter.getSize()>0) {
			baseLineData.addSample(new Sample(data.diameter.getSample(0).getXValue(), 1.75));
			baseLineData.addSample(new Sample(data.diameter.getSample(data.diameter.getSize()-1).getXValue(), 1.75));
		}
		
		curTimeOffset = 0.001;  // force refresh
		graph.primaryXAxis.performAutoScale(true);
		diameterTimeAxis.setRange(graph.primaryXAxis.getRange());
		updateTimeOffset();
		
	

	}
	
	private void updateTimeOffset() {
		double offset = (graph.primaryXAxis.getRange().getLower()-diameterTimeAxis.getRange().getLower())/1000.0;
		if (offset!=curTimeOffset) {
			curTimeOffset = offset;
			graph.primaryXAxis.setTitle("Diameter Time Offset: "+String.format("%.1f", offset)+" seconds");
		}
	}
	
	private void onLoadDataFail() {
		graph.setTitle("ERROR LOADING DATA");
	}
	
	private void loadData(String filePath) {
		graph.setTitle("Loading data, please wait...");
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					data.readFromFile(filePath);				
				} catch (Exception e) {
					e.printStackTrace();
					onLoadDataFail();
					return;
				}
				graph.setTitle(filePath);
//				graph.primaryXAxis.setAutoScale(true);
				onLoadDataSuccess();
			}
		});
		
	}

}
