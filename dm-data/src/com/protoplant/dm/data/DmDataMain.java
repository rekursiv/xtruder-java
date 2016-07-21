package com.protoplant.dm.data;

public class DmDataMain {

//	public static final String dirWithFilesToConvert = "C:/projects/eclipse_mars_workspace/protoplant_java/com.protoplant.xtruder4/data/";
	public static final String dirWithFilesToConvert = "C:/projects/Protoplant/dmdata/";
	
	
	
	public static void main(String[] args) {

		Converter conv = new Converter();
		try {
			conv.convertFilesInDir(dirWithFilesToConvert);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
