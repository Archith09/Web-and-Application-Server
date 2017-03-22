package edu.upenn.cis455.webserver;

//import java.io.*;
import java.util.*;
//import java.nio.file.*;
import javax.servlet.*;

public class MyApplicationContext implements ServletContext {
	public static final String str = "Test Harness";
	public HashMap<String, Object> elements;
	public HashMap<String, String> initialAttributes;

	public MyApplicationContext() {
		elements = new HashMap<String, Object>();
		initialAttributes = new HashMap<String, String>();
	}

	public Object getAttribute(String id) {
		return elements.get(id);
	}

	// not needed
	public String getMimeType(String s) {
		return null;
	}

	// not needed
	public void log(String s) {
		System.err.println(s);
	}

	// not needed
	public void log(String s, Throwable t) {
		System.err.println(s);
		t.printStackTrace(System.err);
	}

	public int getMinorVersion() {
		return 4;
	}

	// not needed
	public ServletContext getContext(String id) {
		return null;
	}

	public String getInitParameter(String id) {
		return initialAttributes.get(id);
	}

	public Enumeration<String> getInitParameterNames() {
		Set<String> hashes = initialAttributes.keySet();
		Vector<String> values = new Vector<String>(hashes);
		return values.elements();
	}

	// not needed
	public RequestDispatcher getNamedDispatcher(String id) {
		return null;
	}

	public String getServerInfo() {
		return HttpServer.author();
	}

	// This is deprecated
	public Servlet getServlet(String s) {
		return null;
	}

	public void removeAttribute(String id) {
		elements.remove(id);
	}
	
	// not needed
	public String getRealPath(String s) {
		return null;
	}

	// This is deprecated
	public Enumeration<String> getServletNames() {
		return null;
	}

	// This is deprecated
	public Enumeration<String> getServlets() {
		return null;
	}

	// not needed
	public RequestDispatcher getRequestDispatcher(String s) {
		return null;
	}

	// not needed
	@SuppressWarnings("rawtypes")
	public java.util.Set getResourcePaths(String s) {
		return null;
	}

	// included non-specific kind
	public Enumeration<String> getAttributeNames() {
		Set<String> hashes = elements.keySet();
		Vector<String> values = new Vector<String>(hashes);
		return values.elements();
	}

	public int getMajorVersion() {
		return 2;
	}

	public void setAttribute(String id, Object name) {
		elements.put(id, name);
	}

	// not needed
	public java.net.URL getResource(String s) {
		return null;
	}

	// not needed
	public java.io.InputStream getResourceAsStream(String s) {
		return null;
	}

	void setInitParam(String id, String content) {
		initialAttributes.put(id, content);
	}

	public String getServletContextName() {
		return str;
	}

	// not needed
	public void log(Exception e, String s) {
		log(s, (Throwable) e);
	}
}
