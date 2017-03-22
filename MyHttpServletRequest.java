package edu.upenn.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.text.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MyHttpServletRequest implements HttpServletRequest {

	public String nothing = "";
	public Hashtable<String, String> firstPath = null;
	public Hashtable<String, String> otherPath = null;
	public ArrayList<Cookie> myArray;
	public MyHttpSession myInstance = null;
	public String myFunction;
	public String version = "ISO-8859-1";
	public MyResponseContainer container;
	public Properties parameters = new Properties();
	public Properties attributes = new Properties();
	
	public MyHttpServletRequest(Hashtable<String, String> firstPath, Hashtable<String, String> otherPath, MyResponseContainer container){
		// TODO Auto-generated method stub
		this.container = container;
		this.firstPath = firstPath;
		this.otherPath = otherPath;
		this.myArray = container.myArray;
		initCookies();
	}

	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		// return null;
		return attributes.get(arg0);
	}

	MyHttpServletRequest(MyHttpSession instance){
		myInstance = instance;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		// return null;
		return attributes.keys();
	}

	// method to initialize cookies
	public void initCookies(){
		String name = otherPath.get("Cookie");
		if(name == null){

		} else if(name != null){
			ArrayList<String> nameIndex = new ArrayList<String>(Arrays.asList(name.split(";")));
			for(String i : nameIndex){
				String shortName = i.trim();
				ArrayList<String> nameLocal = new ArrayList<String>(Arrays.asList(shortName.split("=")));
				myArray.add(new Cookie(nameLocal.get(0), nameLocal.get(1)));
			}
		}
	}

	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		// return null;
		return version;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		// return null;
		return parameters.getProperty(arg0);
	}

	@SuppressWarnings("rawtypes")
	public Map getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {
		// TODO Auto-generated method stub
		// return null;
		return parameters.keys();
	}

	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getScheme() {
		// TODO Auto-generated method stub
		String protocol = "http";
		return protocol;
	}

	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		attributes.put(arg0, arg1);
	}

	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		version = arg0;
	}

	public String getAuthType() {
		// TODO Auto-generated method stub
		return BASIC_AUTH;
	}

	public String getContextPath() {
		// TODO Auto-generated method stub
		// return null;
		return nothing;
	}

	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return myArray.toArray(new Cookie[myArray.size()]);
	}

	public long getDateHeader(String arg0) {
		// TODO Auto-generated method stub
		if(otherPath.get(arg0) != null){
			String curLong = otherPath.get(arg0);
			SimpleDateFormat curFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
			curFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date cur = null;

			try{
				cur = curFormat.parse(curLong);
			} catch(ParseException e){
				System.out.println("ERROR: " + e);
			}
			return cur.getTime();
		} else {
			return -1;
		}
	}

	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return otherPath.get(arg0);
	}

	public Enumeration<String> getHeaderNames() {
		// TODO Auto-generated method stub
		// return null;
		Enumeration<String> headerNames = Collections.enumeration(otherPath.keySet());
		return headerNames;
	}

	public Enumeration<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		// return null;
		LinkedList<String> headers = new LinkedList<String>();
		headers.add(otherPath.get(arg0));
		Enumeration<String> headersList = Collections.enumeration(headers);
		return headersList;
	}

	public int getIntHeader(String arg0) {
		// TODO Auto-generated method stub
		// return 0;
		if(otherPath.get(arg0) != null){
			try{
				return Integer.valueOf(otherPath.get(arg0));
			} catch(NumberFormatException e){
				System.out.println("ERROR: " + e);
				return -2;
			}
		} else {
			return -1;
		}
	}

	public String getMethod() {
		// TODO Auto-generated method stub
		// return null;
		return myFunction;
	}

	public String getPathInfo() {
		// TODO Auto-generated method stub
		// return null;
		// String nothing = "";
		String line = firstPath.get("path");
		System.out.println("You are in getPathInfo method: " + line);
		StringBuilder newLine = new StringBuilder();

		if(line.contains("?")){
			String noAsk = line.substring(0, line.lastIndexOf("?"));
			ArrayList<String> lineSeparate = new ArrayList<String>(Arrays.asList(noAsk.split("/")));
			lineSeparate.remove(0);
			lineSeparate.remove(1);
			for(String i : lineSeparate){
				newLine.append("/");
				newLine.append(i);
			}
			return newLine.toString();
		} else {
			return nothing;
		}
	}

	public void setMethod(String function){
		myFunction = function;
	}

	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQueryString() {
		// TODO Auto-generated method stub
		// return null;
		String line = firstPath.get("path");
		String askLine = line.substring(line.lastIndexOf("?")+1, line.length());
		return askLine;
	}

	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRequestURI() {
		// TODO Auto-generated method stub
		// return null;
		String line = firstPath.get("path");
		if(line == null){
			return null;
		} else{
			return line.split("?")[1];
		}
	}

	public void setParameter(String arg0, String arg1){
		parameters.setProperty(arg0, arg1);
	}

	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		// return null;
		StringBuffer requestURL = new StringBuffer(firstPath.get("path"));
		return requestURL;
	}

	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		// return null;
		if(myInstance == null){
			return null;
		} else{
			return myInstance.getId();
		}
	}

	public void clearParameters(){
		parameters.clear();
	}

	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpSession getSession() {
		// TODO Auto-generated method stub
		boolean isExists = true;
		return getSession(isExists);
	}

	public HttpSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		// return null;
		boolean isExists = false;
		String cur = "JSESSIONID";
		if(!arg0){
			if(!doesInstanceExist()){
				myInstance = null;
			}
		} else {
			if(!doesInstanceExist()){
				for(Cookie myCookie : myArray){
					if(myCookie.getName().equalsIgnoreCase(cur)){
						if(SessionController.fetchSession(myCookie.getValue()) != null){
							isExists = true;
							myInstance = SessionController.fetchSession(myCookie.getValue());
							return myInstance;
						}
					}
				}
				if(isExists){

				} else {
					myInstance = new MyHttpSession();
					Cookie instanceName = new Cookie(cur, Long.toString(myInstance.getSessionId()));
					container.newCookie(instanceName);
					SessionController.newSession(myInstance);
				}
			}
		}
		return myInstance;
	}

	public boolean doesInstanceExist(){
		boolean cur = ((myInstance != null) && myInstance.isTrue());
		return cur;
	}
	
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
