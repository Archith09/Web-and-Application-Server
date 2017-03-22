package edu.upenn.cis455.webserver;

import java.util.*;
import javax.servlet.*;

/*
* Servlet configuration passed to a servlet at the
* time of initialization
*/
public class MyServletConfig implements ServletConfig {

	public String id;
	public MyApplicationContext mac;
	public HashMap<String, String> initParams;

	public MyServletConfig(String id, MyApplicationContext mac){
		this.id = id;
		this.mac = mac;
		initParams = new HashMap<String, String>();
	}

	public String getInitParameter(String id){
		return initParams.get(id);
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getInitParameterNames(){
		Set<String> hashes = initParams.keySet();
		Vector<String> values = new Vector<String>(hashes);
		return values.elements();
	}

	public ServletContext getServletContext(){
		return mac;
	}

	public String getServletName(){
		return id;
	}

	void setInitParam(String id, String content){
		initParams.put(id, content);
	}
}
