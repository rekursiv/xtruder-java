package com.protoplant.xtruder.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import au.com.bytecode.opencsv.CSVReader;

public class JsonDataReader {

	public XYGraph graph = null;
	public CircularBufferDataProvider diameter = new CircularBufferDataProvider(true);
	public CircularBufferDataProvider pressure = new CircularBufferDataProvider(true);	
	public CircularBufferDataProvider velocity = new CircularBufferDataProvider(true);	
	
	private int dataPoints = 0;
	
	public void readFromFile(String filePath) throws Exception {
		diameter.clearTrace();
		diameter.setBufferSize(500000);
		
		pressure.clearTrace();
		pressure.setBufferSize(500000);
		
		velocity.clearTrace();
		velocity.setBufferSize(500000);
		
		dataPoints = 0;
		
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser jp = jsonFactory.createParser(new File(filePath));
		boolean inArray = false;
		while (jp.nextToken()!=null) {
			if (jp.getCurrentToken()==JsonToken.START_ARRAY) inArray=true;
			else if (jp.getCurrentToken()==JsonToken.END_ARRAY) inArray=false;
			else if (inArray) {
				if (jp.getCurrentToken()==JsonToken.START_OBJECT) {
					readDataPoint(jp);
				}
			}
		}
		jp.close();
		
		System.out.println("Read "+dataPoints+" data points.");
		System.out.println("Number of Samples:  Diameter="+diameter.getSize()+"   Pressure="+pressure.getSize()+"   Velocity="+velocity.getSize());
	}
	
	public void readDataPoint(JsonParser jp) throws Exception {
		String type = "";
		double value = 0;
		long ts = 0;
		int bundleIndex = 0;
		String pos = "";
		while (jp.nextToken()!=null && jp.getCurrentToken()!=JsonToken.END_OBJECT) {
//			System.out.println(jp.getText());
			if (jp.getCurrentName().equals("type")) {
				jp.nextToken();
				type = jp.getText();
			} else if (jp.getCurrentName().equals("timestamp")) {
				jp.nextToken();
				ts = jp.getLongValue();
			} else if (jp.getCurrentName().equals("value")) {
				jp.nextToken();
				value = jp.getDoubleValue();
			} else if (jp.getCurrentName().equals("bundle")) {
				jp.nextToken();
				bundleIndex = jp.getIntValue();
			} else if (jp.getCurrentName().equals("pos")) {
				jp.nextToken();
				pos = jp.getText();
			}
		}

		if (type.equals("diameter")) diameter.addSample(new Sample(ts, value));
		else if (type.equals("pressure")) pressure.addSample(new Sample(ts, value));
		else if (type.equals("velocity")) velocity.addSample(new Sample(ts, value));
		else if (type.equals("mark")) {
			Annotation a = new Annotation(bundleIndex+":"+pos, graph.primaryXAxis, graph.primaryYAxis);
			a.setValues(ts, 71.25);
			a.setdxdy(-20, -20);
			a.setShowPosition(false);
			graph.addAnnotation(a);
		}
		++dataPoints;
	}
	
	
	
	
	public void readDataPoint_V1(JsonParser jp) throws Exception {
		String type = "";
		double value = 0;
		long ts = 0;
		int bundleIndex = 0;
		int stickIndex = 0;
		while (jp.nextToken()!=null && jp.getCurrentToken()!=JsonToken.END_OBJECT) {
//			System.out.println(jp.getText());
			if (jp.getCurrentName().equals("type")) {
				jp.nextToken();
				type = jp.getText();
			} else if (jp.getCurrentName().equals("timestamp")) {
				jp.nextToken();
				ts = jp.getLongValue();
			} else if (jp.getCurrentName().equals("value")) {
				jp.nextToken();
				value = jp.getDoubleValue();
			} else if (jp.getCurrentName().equals("bundle-index")) {
				jp.nextToken();
				bundleIndex = jp.getIntValue();
			} else if (jp.getCurrentName().equals("stick-index")) {
				jp.nextToken();
				stickIndex = jp.getIntValue();
			}
		}
//		System.out.println("-------");
//		type = jp.getCurrentName();
		
//		System.out.println(jp.getText());
		if (type.equals("diameter")) diameter.addSample(new Sample(ts, value));
		else if (type.equals("pressure")) pressure.addSample(new Sample(ts, value));
		else if (type.equals("velocity")) velocity.addSample(new Sample(ts, value));
		else if (type.equals("cut")) {
			Annotation a = new Annotation(bundleIndex+":"+stickIndex, graph.primaryXAxis, graph.primaryYAxis);
			a.setValues(ts, 71.25);
			a.setdxdy(-20, -20);
			a.setShowPosition(false);
			graph.addAnnotation(a);
		}
		++dataPoints;
	}
	
	
	
	private long timeStampToMilis(String ts) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss_SSS");
		return dtf.parseDateTime(ts).getMillis();
	}
	

}
