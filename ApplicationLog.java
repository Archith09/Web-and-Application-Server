package edu.upenn.cis455.webserver;

import java.util.Date;

public class ApplicationLog {
	public static StringBuffer errorBuf = new StringBuffer();
	
	public synchronized static void addToError(String s){
//		System.out.println("adding to error log");
		errorBuf.append("\n");
		errorBuf.append(s);
		errorBuf.append("\nDate: ");
		errorBuf.append(new Date().toString());
		errorBuf.append("\n\n");
	}
	
	public static String getErrorFile(){
		return errorBuf.toString();
	}
}
