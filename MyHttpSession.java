package edu.upenn.cis455.webserver;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class MyHttpSession implements HttpSession {

	public Cookie cur;
	public long begin = 0;
	public long prevEntry = 0;
	public long instance = 0;
	public static long allInstances = 0;
	public boolean isTrue = true;
	public Properties attributes = new Properties();
	public String instanceID = "JSESSIONID";

	// constructor for MyHttpSession Class
	public MyHttpSession(){
		instance = allInstances + 1;
		allInstances = allInstances + 1;
		cur = new Cookie(instanceID, Long.toString(instance));
	}

	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		// return null;
		return attributes.get(arg0);
	}


	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		// return null;
		return attributes.keys();
	}


	public long getCreationTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Cookie getCookie(){
		return cur;
	}

	public String getId() {
		// TODO Auto-generated method stub
		// return null;
		return Long.toString(instance);
	}


	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		// return 0;
		return prevEntry;
	}


	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isTrue(){
		return isTrue;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getSessionId(){
		return instance;
	}

	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}


	public Object getValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}


	public void invalidate() {
		// TODO Auto-generated method stub
		isTrue = false;
	}


	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}


	public void putValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		attributes.put(arg0, arg1);
	}


	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		attributes.remove(arg0);
	}


	public void removeValue(String arg0) {
		// TODO Auto-generated method stub
		attributes.remove(arg0);
	}


	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		attributes.put(arg0, arg1);
	}


	public void setMaxInactiveInterval(int arg0) {
		// TODO Auto-generated method stub

	}

}
