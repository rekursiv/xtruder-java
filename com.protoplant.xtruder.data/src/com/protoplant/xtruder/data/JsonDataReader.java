package com.protoplant.xtruder.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import au.com.bytecode.opencsv.CSVReader;

public class JsonDataReader {

	
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
		while (jp.nextToken()!=null && jp.getCurrentToken()!=JsonToken.END_OBJECT) {
//			System.out.println(jp.getText());
			if (jp.getCurrentName().equals("type")) {
				jp.nextToken();
				type = jp.getText();
//				System.out.println("D "+jp.getText());
			} else if (jp.getCurrentName().equals("timestamp")) {
				jp.nextToken();
				ts = jp.getLongValue();
//				System.out.println("T "+jp.getLongValue());
			} else if (jp.getCurrentName().equals("value")) {
				jp.nextToken();
				value = jp.getDoubleValue();
//				System.out.println("V "+jp.getDoubleValue());
			}
		}
//		System.out.println("-------");
//		type = jp.getCurrentName();
		
//		System.out.println(jp.getText());
		if (type.equals("diameter")) diameter.addSample(new Sample(ts, value));
		else if (type.equals("pressure")) pressure.addSample(new Sample(ts, value));
		else if (type.equals("velocity")) velocity.addSample(new Sample(ts, value));
		++dataPoints;
	}
	
	public void _readFromFile(String filePath) throws Exception {
		
		diameter.clearTrace();
		diameter.setBufferSize(500000);
		
		pressure.clearTrace();
		pressure.setBufferSize(500000);
		
		velocity.clearTrace();
		velocity.setBufferSize(500000);
		
		CSVReader reader = new CSVReader(new FileReader(filePath));
		
		String[] row = reader.readNext();  
		int rowCount = 0;
		while (row!=null) {
			if (rowCount>0) {  // first line is header
				if (row[1].equals("Diameter") && row.length==3) {
					diameter.addSample(new Sample(timeStampToMilis(row[0]), Float.parseFloat(row[2])));
//					diameter.addSample(new Sample(timeStampToMilis(row[0])-24000, Float.parseFloat(row[2])));
				} else if (row[1].equals("Pressure") && row.length==3) {
					pressure.addSample(new Sample(timeStampToMilis(row[0]), Float.parseFloat(row[2])));
				} else if (row[1].equals("Motor") && row[2].equals("Roller")) {
					velocity.addSample(new Sample(timeStampToMilis(row[0]), Float.parseFloat(row[3])));
				} 
//				else System.out.println("*** UNMAPPED:  "+Arrays.toString(row));
			}
			row = reader.readNext();
			++rowCount;
//			if (rowCount>100) break;
		}
		
		reader.close();
		
		System.out.println("Number of Rows: "+rowCount);
		System.out.println("Number of Samples:  Diameter="+diameter.getSize()+"   Pressure="+pressure.getSize()+"   Velocity="+velocity.getSize());
		
//		diameter.triggerUpdate();
//		pressure.triggerUpdate();
//		velocity.triggerUpdate();
	}
	
	
	private long timeStampToMilis(String ts) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss_SSS");
		return dtf.parseDateTime(ts).getMillis();
	}
	

}
