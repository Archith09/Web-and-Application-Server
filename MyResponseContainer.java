package edu.upenn.cis455.webserver;

import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.http.Cookie;

// class to organize the response
public class MyResponseContainer {
	public HashMap<String, ArrayList<String>> firstPath = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ArrayList<String>> otherPath = new HashMap<String, ArrayList<String>>();
	public String emptyContent = "";
	public String emptyFirstPath = "";
	public String emptyOtherPath = "";
	public String nothing = "";
	public StringBuilder replyFirstPath = new StringBuilder();
	public StringBuilder replyOtherPath = new StringBuilder();
	public ArrayList<Cookie> myArray = new ArrayList<Cookie>();
	public StringBuilder cookieOtherPath = new StringBuilder();
	public StringBuilder content = new StringBuilder();
	public int isEmpty = 0;
	public String yes = "Set-Cookie";
	public String sep = ",";
	public String sep2 = ":";
	public String sep3 = "=";
	public String space = " ";
	public String newLine = "\n";
	public String hv = "httpVersion";
	public String rsc = "responseStatusCode";
	public String rsd = "responseStatusDescription";
	public String dateFormat = "EEE, dd MMM yyyy HH:mm:ss z";
	public String timeZone = "GMT";

	public void emptyContainer() {
		emptyFirstPath = replyFirstPath.toString();
		emptyOtherPath = replyOtherPath.toString();
		emptyContent = content.toString();
		System.out.println(replyFirstPath + "\n" + emptyFirstPath);
	}

	public void emptyContainer(int containerLength){
		emptyFirstPath = replyFirstPath.toString();
		emptyOtherPath = replyOtherPath.toString();
		if(containerLength > isEmpty){
			emptyContent = content.toString().substring(isEmpty, containerLength);
		} else {
			emptyContent = content.toString();
		}
	}

	// validate the initial line
	public void checkFirstPath(HashMap<String, ArrayList<String>> firstPath){
		replyFirstPath.append(toSeries(firstPath.get(hv)));
		replyFirstPath.append(space);
		replyFirstPath.append(toSeries(firstPath.get(rsc)));
		replyFirstPath.append(space);
		replyFirstPath.append(toSeries(firstPath.get(rsd)));
		replyFirstPath.append("\r\n");
	}

	// validate the header lines
	public void checkOtherPath(HashMap<String, ArrayList<String>> otherPath){
		for(String index : otherPath.keySet()){
			if(index.equals(yes)){
				for(String iterator : otherPath.get(index)){
					replyOtherPath.append(index + sep2 + iterator + "\r\n");
				}
			} else {
				replyOtherPath.append(index + sep2 + toSeries(otherPath.get(index)) + "\r\n");
			}
		}
		replyOtherPath.append(newLine);
	}

	// method to convert arraylist to string
	public String toSeries(ArrayList<String> input){
		ArrayList<String> output = new ArrayList<String>(input);
		StringBuilder outputString = new StringBuilder();
		if(input.isEmpty()){
			return nothing;
		} else {
			outputString.append(output.remove(0));
			for(String temp : output){
				outputString.append(sep);
				outputString.append(temp);
			}
			return outputString.toString();
		}
	}

	// method to send response back
	public String emptyReply(){
		return emptyFirstPath + updateCookie() + emptyOtherPath + emptyContent;
	}

	// method to set cookie header lines
	@SuppressWarnings("static-access")
	public String updateCookie(){
		for(Cookie iterator : myArray){
			int elapsed = iterator.getMaxAge();
			if(elapsed == -1){
				cookieOtherPath.append(yes + sep2 + iterator.getName() + sep3 + iterator.getValue() + "\r\n");
			} else {
				SimpleDateFormat cur = new SimpleDateFormat(dateFormat);
				cur.setTimeZone(TimeZone.getTimeZone(timeZone));

				Date today = new Date();
				long timeInMs = today.getTime();
				long lapseTimeInMs = timeInMs + ((long) elapsed) * 1000;
				Date lapseDate = new Date(lapseTimeInMs);
				String lapseDateToSeries = dateFormat.format(lapseDate.toString());
				System.out.println("Welcome!\nCurrent Time: " + dateFormat.format(today.toString()) + "\n" + yes + sep2 + iterator.getName() + sep3 + iterator.getValue() + ";" + "\n" + "Expires: " + lapseDateToSeries);
				cookieOtherPath.append(yes);
				cookieOtherPath.append(sep2);
				cookieOtherPath.append(iterator.getName());
				cookieOtherPath.append(sep3);
				cookieOtherPath.append(iterator.getValue());
				cookieOtherPath.append(";");
				cookieOtherPath.append("expires= ");
				cookieOtherPath.append(lapseDateToSeries + "\r\n");
			}
		}
		return cookieOtherPath.toString();
	}

	// method to send initial line
	public String fetchEmptyFirstPath(){
		return emptyFirstPath;
	}

	// method to send header lines
	public String fetchEmptyOtherPath(){
		return emptyOtherPath;
	}

	// method to send content
	public String fetchEmptyContent(){
		return emptyContent;
	}

	public void add(int response){
		content.append(nothing + response);
	}

	public void add(long response){
		content.append(nothing + response);
	}

	public void add(double response){
		content.append(nothing + response);
	}

	public void add(float response){
		content.append(nothing + response);
	}

	public void add(String response){
		content.append(response);
	}

	public void add(char[] response){
		content.append(new String(response));
	}

	public void add(char response){
		content.append(nothing + response);
	}

	public void add(boolean response){
		content.append(nothing + response);
	}

	// method to add new cookie to array list
	public void newCookie(Cookie c){
		myArray.add(c);
	}

	// method to add header lines
	public void updateOtherPath(String first, String second){
		if(otherPath.get(first) != null){
			ArrayList<String> newOtherPath = otherPath.get(first);
			newOtherPath.add(first);
		} else {
			ArrayList<String> newOtherPath = new ArrayList<String>();
			newOtherPath.add(first);
			otherPath.put(first, newOtherPath);
		}
	}

}
