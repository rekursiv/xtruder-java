package com.protoplant.xtruder.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import au.com.bytecode.opencsv.CSVReader;

public class DataReader {

	
	public CircularBufferDataProvider diameter = new CircularBufferDataProvider(true);
	public CircularBufferDataProvider pressure = new CircularBufferDataProvider(true);	
	public CircularBufferDataProvider velocity = new CircularBufferDataProvider(true);	
	
	public void readFromFile(String filePath) throws Exception {
		
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
