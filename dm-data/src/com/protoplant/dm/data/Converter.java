package com.protoplant.dm.data;

import java.io.File;
import java.io.FileWriter;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.opencsv.CSVWriter;

public class Converter {

	private int dataPoints;
	private CSVWriter cvsWriter;
//	private String dirPath = "";
	

	public void convertFilesInDir(String dirPath) throws Exception {
//		this.dirPath = dirPath;
		
		File[] fileList = new File(dirPath).listFiles();
		for (File curFile : fileList) {
			if (curFile.isFile() && curFile.getName().endsWith(".js")) convertFromFile(curFile);
		}
		
	}
	
	private void convertFromFile(File jsonFile) throws Exception {
		dataPoints = 0;
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser jp = jsonFactory.createParser(jsonFile);
		boolean inArray = false;
		while (jp.nextToken()!=null) {
			if (jp.getCurrentToken()==JsonToken.START_ARRAY) inArray=true;
			else if (jp.getCurrentToken()==JsonToken.END_ARRAY) inArray=false;
			else if (inArray) {
				if (jp.getCurrentToken()==JsonToken.START_OBJECT) {
//					readDataPoint_V1(jp);
					readDataPoint(jp);
				}
			} else if (jp.getCurrentName()!=null&&jp.getCurrentName().equals("material")) {
				jp.nextToken();
				System.out.println(jp.getText());
				String csvFilePath = jsonFile.getPath();
				csvFilePath = csvFilePath.substring(0, csvFilePath.length()-3);
				csvFilePath = csvFilePath+jp.getText().substring(3)+".csv";
				System.out.println("Creating new CSV file: "+csvFilePath);
				cvsWriter = new CSVWriter(new FileWriter(csvFilePath));
			}
		}
		jp.close();
		cvsWriter.close();
		
		System.out.println("Converted "+dataPoints+" data points.");
	}
	
	private void readDataPoint(JsonParser jp) throws Exception {
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

		if (type.equals("mark")) {
			cvsWriter.writeNext(new String[]{type,""+ts,bundleIndex+":"+pos});
		} else {
			cvsWriter.writeNext(new String[]{type,""+ts,""+value});
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
		
		if (type.equals("cut")) {
			cvsWriter.writeNext(new String[]{type,""+ts,bundleIndex+":"+stickIndex});
		} else {
			cvsWriter.writeNext(new String[]{type,""+ts,""+value});
		}
		
		++dataPoints;
	}
	
	
	
}
