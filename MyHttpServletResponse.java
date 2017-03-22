package edu.upenn.cis455.webserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MyHttpServletResponse implements HttpServletResponse {

	private MyResponseContainer container;
	protected HashMap<String, ArrayList<String>> firstPath;
	protected HashMap<String, ArrayList<String>> otherPath;
	protected ArrayList<Cookie> myArray;

	// constructor for MyHttpServletResponse class
	public MyHttpServletResponse(MyResponseContainer container){
		this.container = container;
		firstPath = container.firstPath;
		otherPath = container.otherPath;
		myArray = container.myArray;
		ArrayList<String> protocol = new ArrayList<String>();
		protocol.add("HTTP/1.1");
		firstPath.put("httpVersion", protocol);
		ArrayList<String> rsc = new ArrayList<String>();
		rsc.add("200");
		firstPath.put("responseStatusCode", rsc);
		ArrayList<String> rsd = new ArrayList<String>();
		rsd.add("OK");
		firstPath.put("responseStatusDescription", rsd);
	}

	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		container.checkFirstPath(firstPath);
		container.checkOtherPath(otherPath);
		container.emptyContainer();
	}

	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return (PrintWriter) (new MyConsole(container, this));
	}

	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub

	}

	public MyResponseContainer getMyResponseContainer(){
		return container;
	}

	public void setCharacterEncoding(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub

	}

	public void setContentType(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub

	}

	public void addCookie(Cookie arg0) {
		// TODO Auto-generated method stub
		for(Cookie i : myArray){
			if(arg0.getName().equals(i.getName()) && arg0.getValue().equals(i.getValue())){
				System.err.println("The cookie " + i + " already exists.");
				return;
			}
		}
		myArray.add(arg0);
	}

	public void addDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	public void addHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		if(otherPath.get(arg0) != null){
			ArrayList<String> newOtherPath = otherPath.get(arg0);
			newOtherPath.add(arg1);
		} else {
			ArrayList<String> newOtherPath = (new ArrayList<String>());
			newOtherPath.add(arg1);
			otherPath.put(arg0, newOtherPath);
		}
	}

	public void addIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public boolean containsHeader(String arg0) {
		// TODO Auto-generated method stub
		// return false;
		if(otherPath.get(arg0) != null){
			return true;
		} else {
			return false;
		}
	}

	public String encodeRedirectURL(String arg0) {
		// TODO Auto-generated method stub
		// return null;
		return arg0;
	}

	public String encodeRedirectUrl(String arg0) {
		// TODO Auto-generated method stub
		// return null;
		return arg0;
	}

	public String encodeURL(String arg0) {
		// TODO Auto-generated method stub
		// return null;
		return arg0;
	}

	public String encodeUrl(String arg0) {
		// TODO Auto-generated method stub
		// return null;
		return arg0;
	}

	public void sendError(int arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public void sendError(int arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub

	}

	public void sendRedirect(String arg0) throws IOException {
		// TODO Auto-generated method stub
//		Exception fault = new Exception();
//		System.out.println("Sending redirect to " + arg0 + " as requested\n" + "Details: ");
//		StackTraceElement[] element = fault.getStackTrace();
//		for(int j = 0; j < element.length; j++){
//			System.out.println(element[j].toString());
//		}
		System.out.println("Sending redirect to " + arg0 + " as requested\n" + "Details: ");
		setStatus(302, "FOUND");
		setHeader("Location", arg0);
		//Location: arg0;
	}

	public void setDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		if(otherPath.get(arg0) != null){
			ArrayList<String> newOtherPath = otherPath.get(arg0);
			newOtherPath.clear();
			newOtherPath.add(arg1);
		} else {
			ArrayList<String> newOtherPath = (new ArrayList<String>());
			newOtherPath.clear();
			newOtherPath.add(arg1);
			otherPath.put(arg0, newOtherPath);
		}
	}

	public void setIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void setStatus(int arg0) {
		// TODO Auto-generated method stub
		if(arg0 == 200) {
			ArrayList<String> protocol = new ArrayList<String>();
			protocol.add("HTTP/1.1");
			firstPath.put("httpVersion", protocol);
			ArrayList<String> resCode = new ArrayList<String>();
			resCode.add("200");
			firstPath.put("responseStatusCode", resCode);
			ArrayList<String> resDesc = new ArrayList<String>();
			resDesc.add("OK");
			firstPath.put("responseStatusDescription", resDesc);
		}
	}

	public void setStatus(int arg0, String arg1) {
		// TODO Auto-generated method stub
		ArrayList<String> protocol = new ArrayList<String>();
		protocol.add("HTTP/1.1");
		firstPath.put("httpVersion", protocol);
		ArrayList<String> resCode = new ArrayList<String>();
		resCode.add(""+arg0+"");
		firstPath.put("responseStatusCode", resCode);
		ArrayList<String> resDesc = new ArrayList<String>();
		resDesc.add(arg1);
		firstPath.put("responseStatusDescription", resDesc);
//		System.out.println("First Path: " + firstPath.toString());
	}

}
