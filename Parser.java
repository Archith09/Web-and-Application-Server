package edu.upenn.cis455.webserver;

import java.io.*;
//import java.net.*;
import java.util.*;
import java.text.*;

public class Parser {

	public static String kind = null;
	public static String changedDay = null;
	public static boolean changeValidation = true;
	public static boolean isModified = true;
	public static SimpleDateFormat latest = null;
	public static final boolean positive = true;
	public static final boolean negative = false;
	public static String usableDateFormat = null;
	// public static Date formattedDate = null;
	public static String threeZeroFour = "304 Not Modified";
	public static String fourOneTwo = "412 Precondition Failed";
	public static char sep = ',';
	public static String dateFormat1 = "EEE, dd MMM yyyy HH:mm:ss z";
	public static String dateFormat2 = "E, dd-MMM-yy HH:mm:ss z";
	public static String dateFormat3 = "EEE MMM dd HH:mm:ss yyyy";
	public static String tz = "GMT";
	public static String ims = "If-Modified-Since";
	public static String ius = "If-Unmodified-Since";

	public static void compose(Hashtable<String, String> reqInfo, Hashtable<String, String> incoming, File outgoing) 
	throws ParseException{
		String http = incoming.get("HTTP");
		String get = incoming.get("GET");
		String path = incoming.get("path");
		String expect = incoming.get("expect");

		if(http != null){
			reqInfo.put("HTTP", http);
			if(get != null){
				reqInfo.put("GET", get);
			}
			else if(path != null){
				reqInfo.put("path", path);
			}
		}
		else if(expect != null){
			reqInfo.put("expect", expect);
		}
		latest = new SimpleDateFormat(dateFormat1);
		latest.setTimeZone(TimeZone.getTimeZone(tz));
		reqInfo.put("Date", latest.format(new Date()));

		isModified = lastChanged(incoming, outgoing);
		String yes = "true";
		String changed = "modify";
		if(!isModified){
			if(incoming.get(ims) != null){
				reqInfo.put(changed, threeZeroFour);
			}
			else if(incoming.get(ims) != null){
				reqInfo.put(changed, fourOneTwo);
			}
			else{
				System.out.println("ERROR");
			}
		}
		else if(isModified){
			reqInfo.put(changed, yes);
		}
	}

	public boolean authentication(Hashtable<String, String> incoming){
		if(incoming.containsKey("Host")){
			return true;
		}
		else{
			return false;
		}
	}

	public static boolean lastChanged(Hashtable<String, String> incoming, File outgoing) 
	throws ParseException{
		String modified = incoming.get(ims);
		String unModified = incoming.get(ims);
		if(modified != null){
			kind = ims;
			changedDay = modified;
			changeValidation = verifyChangeDay(kind, convert(changedDay), outgoing);
		}
		else if(unModified != null){
			kind = ims;
			changedDay = unModified;
			changeValidation = verifyChangeDay(kind, convert(changedDay), outgoing);
		}
		else changeValidation = true;
		return changeValidation;
	}

	public static Date convert(String changedDay)
	throws ParseException{
		usableDateFormat = changedDay.toUpperCase();
		if(usableDateFormat.contains(tz)){
			if(usableDateFormat.charAt(3) == ','){
				latest = new SimpleDateFormat(dateFormat1);
			}
			else if(usableDateFormat.charAt(6) == ','){
				latest = new SimpleDateFormat(dateFormat2);
			}
			else if(usableDateFormat.charAt(7) == ','){
				latest = new SimpleDateFormat(dateFormat2);
			}
		}
		else if(!usableDateFormat.contains(",") && !usableDateFormat.contains("-")){
			latest = new SimpleDateFormat(dateFormat3);
		}
		return latest.parse(changedDay);
	}

	public static boolean verifyChangeDay(String kind, Date changedDay, File outgoing){
		latest = new SimpleDateFormat(dateFormat1);
		latest.setTimeZone(TimeZone.getTimeZone(tz));
		if(kind.equalsIgnoreCase(ims)){
			if(changedDay.getTime() > outgoing.lastModified()){
				return positive;
			}
			else return negative;
		}
		else if(kind.equalsIgnoreCase(ims)){
			if(changedDay.getTime() < outgoing.lastModified()){
				System.out.println(positive);
				return positive;
			}
			else {
				System.out.println(negative);
				return negative;
			}
		}
		return positive;
	}

	// public String toString() {
 //    	return value ? "TRUE" : "FALSE";
	// }
	
}
