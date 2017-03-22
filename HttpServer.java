package edu.upenn.cis455.webserver;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/*
 * Methods analyzeXmlFile1, analyzeXmlFile2, usage
 * makeScene, makeExtensions, Handler1, Handler2
 * and initial check for argument list
 * are adopted from TestHarness file provided
 * on course web page.
 * @author Archith Shivanagere Muralinath
 * 
 */
class HttpServer extends HttpServlet {

	public static int portNo = 8080;
	public static String rootDir = "";
	// public static volatile boolean serverStatus = true;
	public static int numThreads = 10000;
	public static ServerSocket server = null;

	public static String author = "Archith Shivanagere Muralinath's Server";
	public Thread currentWorker = null;
	// public static ApplicationRegister applicationRegister;
	public static SessionController sc;
	public static String xmlFileAddress = null;
	public static MyThreadPool newTP = null;
	public static boolean running = false;
	public static final long serialVersionUID = 1L;
	public static HashMap<String, HttpServlet> extensions = null;
	public static HashMap<String, String> extensionsAddress = null;
	public static final int qLength = 19000;

	public static void main(String args[]) throws IOException {
		if (args.length < 3 || args.length % 2 == 0) {
			usage();
			System.exit(-1);
		}
		portNo = Integer.parseInt(args[0]);
		rootDir = args[1];
		xmlFileAddress = args[2];

		// applicationRegister = ApplicationRegister.appCase(rootDir +
		// "applicationregister.txt");
		newTP = new MyThreadPool(10, 100000);
		sc = SessionController.sessionCase();
		Worker.filesFolderLocation(rootDir);
		establishServerConnection();

		System.out.println("Name: Archith Shivanagere Muralinath" + "\nPennKey: archith");
		System.out.println("Port Number: " + portNo + "\nRoot Directory: " + rootDir + "\n");
		Worker.filesFolderLocation(rootDir);

		executeExtensions();
		execute();
	}

	private static void usage() {
		System.err.println("usage: java TestHarness <path to web.xml> " + "[<GET|POST> <servlet?params> ...]");
	}

	// fetch server information
	public static String author() {
		return author;
	}

	// establishing server side socket
	public static void establishServerConnection() throws IOException {
		try {
			server = new ServerSocket(portNo, qLength);
		} catch (IOException e) {
			// applicationRegister.updateRegister(e.toString());
			ApplicationLog.addToError(e.toString());
			throw new RuntimeException("Unable to establish server socket on port " + portNo, e);
		}
	}

	// run server
	public static void execute() throws IOException {
		while (!running) {
			Socket client = null;
			try {
				client = server.accept();
			} catch (SocketException e) {
				if (running()) {
					// applicationRegister.updateRegister(e.toString());
					ApplicationLog.addToError(e.toString());
					System.out.println("Connection is closed");
					return;
				}
				throw new RuntimeException("Unable to accept incoming connection. Error: ", e);
			} catch (IOException e) {
				// applicationRegister.updateRegister(e.toString());
				ApplicationLog.addToError(e.toString());
			}
			newTP.establishConnection(client);
		}
		System.out.println("Server is shutdown.");
	}

	// load servlet
	public static void executeExtensions() throws IOException {
		Handler1 Handler1 = null;
		Handler2 Handler2 = null;
		try {
			Handler1 = analyzeXmlFile1(xmlFileAddress);
			Handler2 = analyzeXmlFile2(xmlFileAddress);
		} catch (Exception e) {
			// applicationRegister.updateRegister(e.toString());
			ApplicationLog.addToError(e.toString());
		}

		MyApplicationContext scene = makeScene(Handler1);
		try {
			extensions = makeExtensions(Handler1, scene);
			setExtensionAddress(Handler2);
		} catch (Exception e) {
			// applicationRegister.updateRegister(e.toString());
			ApplicationLog.addToError(e.toString());
		}
	}

	// parse web.xml file
	public static Handler1 analyzeXmlFile1(String xmlFile) throws Exception {
		boolean empty = false;
		Handler1 handler1 = new Handler1();
		File content = new File(xmlFile);
//		System.out.println(xmlFile);
		if (content.exists() == empty) {
			System.err.println("ERROR: Unable to locate file " + xmlFile + " at " + content.getPath());
			System.exit(-1);
		}
		SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
		sp.parse(content, handler1);
//		System.out.println(handler1.m_servlets.toString());
//		System.out.println(handler1.m_servletParams.toString());
		return handler1;
	}

	// parse web.xml file
	public static Handler2 analyzeXmlFile2(String xmlFile) throws Exception {
		boolean empty = false;
		Handler2 handler2 = new Handler2();
		File content = new File(xmlFile);
		if (content.exists() == empty) {
			System.err.println("ERROR: Unable to locate file " + xmlFile + " at " + content.getPath());
			System.exit(-1);
		}
		SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
		sp.parse(content, handler2);
		return handler2;
	}

	// create context
	public static MyApplicationContext makeScene(Handler1 handler1) {
		MyApplicationContext mac = new MyApplicationContext();
		for (String i : handler1.m_contextParams.keySet()) {
			mac.setInitParam(i, handler1.m_contextParams.get(i));
		}
		return mac;
	}

	// check if server is running
	public synchronized static boolean running() {
		return running;
	}

	// close all servlets and server socket
	public static void serverShutdown() {
		running = true;
		try {
			destroyExtensions();
			server.close();
			MyThreadPool.destroyConnection();
			// applicationRegister.saveDocument();
			System.out.println("Closing Application.");
		} catch (IOException e) {
			// applicationRegister.updateRegister(e.toString());
			ApplicationLog.addToError(e.toString());
		}
	}

	@SuppressWarnings("rawtypes")
	private static HashMap<String, HttpServlet> makeExtensions(Handler1 h, MyApplicationContext fc) throws Exception {
		HashMap<String, HttpServlet> servlets = new HashMap<String, HttpServlet>();
		for (String servletName : h.m_servlets.keySet()) {
			MyServletConfig config = new MyServletConfig(servletName, fc);
			String className = h.m_servlets.get(servletName);
			Class servletClass = Class.forName(className);
			HttpServlet servlet = (HttpServlet) servletClass.newInstance();
			HashMap<String, String> servletParams = h.m_servletParams.get(servletName);
			if (servletParams != null) {
				for (String param : servletParams.keySet()) {
					config.setInitParam(param, servletParams.get(param));
				}
			}
			servlet.init(config);
			servlets.put(servletName, servlet);
		}
		return servlets;
	}

	// return servlet url
	public static HashMap<String, String> returnExtensionAddress() {
		return extensionsAddress;
	}

	// fetch all servlets
	public static HashMap<String, HttpServlet> existingExtensions() {
		return extensions;
	}

	// destroy all servlets
	public static void destroyExtensions() {
		// check if this works without "HttpServer"
		for (String extension : HttpServer.existingExtensions().keySet()) {
			HttpServer.existingExtensions().get(extension).destroy();
		}
	}

	// static class for first handler
	static class Handler1 extends DefaultHandler {

		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (qName.compareTo("servlet") == 0) {
				m_state = 1;
			} else if (qName.compareTo("servlet-mapping") == 0) {
				m_state = 2;
			} else if (qName.compareTo("context-param") == 0) {
				m_state = 3;
			} else if (qName.compareTo("init-param") == 0) {
				m_state = 4;
			} else if (qName.compareTo("param-name") == 0) {
				m_state = (m_state == 3) ? 10 : 20;
			} else if (qName.compareTo("param-value") == 0) {
				m_state = (m_state == 10) ? 11 : 21;
			} else if (qName.compareTo("servlet-name") == 0) {
				m_state = (m_state == 1) ? 300 : 400;
			} else if (qName.compareTo("servlet-class") == 0) {
				m_state = 301;
			} else if (qName.compareTo("url-pattern") == 0) {
				m_state = 401;
			}
		}

		public void characters(char[] ch, int start, int length) {
			String value = new String(ch, start, length);
			if (m_state == 1) {
				m_servletName = value;
				m_state = 0;
			} else if (m_state == 2) {
				m_servlets.put(m_servletName, value);
				m_state = 0;
			} else if (m_state == 10 || m_state == 20) {
				m_paramName = value;
			} else if (m_state == 11) {
				if (m_paramName == null) {
					System.err.println("Context parameter value '" + value + "' without name");
					System.exit(-1);
				}
				m_contextParams.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 21) {
				if (m_paramName == null) {
					System.err.println("Servlet parameter value '" + value + "' without name");
					System.exit(-1);
				}
				HashMap<String, String> p = m_servletParams.get(m_servletName);
				if (p == null) {
					p = new HashMap<String, String>();
					m_servletParams.put(m_servletName, p);
				}
				p.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 300) {
				m_state = 0;
				m_servletName = value;
			} else if (m_state == 301) {
				m_state = 0;
				m_servlets.put(m_servletName, value);
			} else if (m_state == 400) {
				m_state = 0;
				m_servletNameNew = value;
			} else if (m_state == 401) {
				m_state = 0;
				m_address.put(value, m_servletNameNew);
			}
		}

		private int m_state = 0;
		private String m_servletName;
		private String m_paramName;
		private String m_servletNameNew;
		HashMap<String, String> m_servlets = new HashMap<String, String>();
		HashMap<String, String> m_contextParams = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> m_servletParams = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> m_address = new HashMap<String, String>();
	}

	// static class for first handler
	static class Handler2 extends DefaultHandler {

		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (qName.compareTo("servlet") == 0) {
				m_state = 1;
			} else if (qName.compareTo("servlet-mapping") == 0) {
				m_state = 2;
			} else if (qName.compareTo("context-param") == 0) {
				m_state = 3;
			} else if (qName.compareTo("init-param") == 0) {
				m_state = 4;
			} else if (qName.compareTo("param-name") == 0) {
				m_state = (m_state == 3) ? 10 : 20;
			} else if (qName.compareTo("param-value") == 0) {
				m_state = (m_state == 10) ? 11 : 21;
			} else if (qName.compareTo("servlet-name") == 0) {
				m_state = (m_state == 1) ? 300 : 400;
			} else if (qName.compareTo("servlet-class") == 0) {
				m_state = 301;
			} else if (qName.compareTo("url-pattern") == 0) {
				m_state = 401;
			}
		}

		public void characters(char[] ch, int start, int length) {
			String value = new String(ch, start, length);
			if (m_state == 1) {
				m_servletName = value;
				m_state = 0;
			} else if (m_state == 2) {
				m_servlets.put(m_servletName, value);
				m_state = 0;
			} else if (m_state == 10 || m_state == 20) {
				m_paramName = value;
			} else if (m_state == 11) {
				if (m_paramName == null) {
					System.err.println("Context parameter value '" + value + "' without name");
					System.exit(-1);
				}
				m_contextParams.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 21) {
				if (m_paramName == null) {
					System.err.println("Servlet parameter value '" + value + "' without name");
					System.exit(-1);
				}
				HashMap<String, String> p = m_servletParams.get(m_servletName);
				if (p == null) {
					p = new HashMap<String, String>();
					m_servletParams.put(m_servletName, p);
				}
				p.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 300) {
				m_state = 0;
				m_servletName = value;
			} else if (m_state == 301) {
				m_state = 0;
				m_servlets.put(m_servletName, value);
			} else if (m_state == 400) {
				m_state = 0;
				m_servletNameNew = value;
			} else if (m_state == 401) {
				m_state = 0;
				m_address.put(value, m_servletNameNew);
			}
		}

		private int m_state = 0;
		private String m_servletName;
		private String m_paramName;
		private String m_servletNameNew;
		HashMap<String, String> m_servlets = new HashMap<String, String>();
		HashMap<String, String> m_contextParams = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> m_servletParams = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> m_address = new HashMap<String, String>();
	}

	public static void setExtensionAddress(Handler2 handler2) {
		extensionsAddress = handler2.m_address;
	}

	// methods for control panel
	public static Hashtable<Long, Thread.State> situation() {
		Hashtable<Long, Thread.State> workersDetails = MyThreadPool.findThreadState();
		return workersDetails;
	}

	public static Hashtable<Long, String> position() {
		Hashtable<Long, String> workersDetails = MyThreadPool.findThreadStatus();
		return workersDetails;
	}

	public static Set<Long> workersList() {
		Hashtable<Long, Thread.State> workersDetails = MyThreadPool.findThreadState();
		return workersDetails.keySet();
	}

	// fetch session controller
	public static SessionController returnSessionController() {
		return sc;
	}

}