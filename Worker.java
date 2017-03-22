package edu.upenn.cis455.webserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.nio.file.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class Worker extends Thread {

	private boolean workerStatus = false;
	public static String url = "/";
	public String uri = null;
	public static MyBlockingQueue mbq = null;
	public String firstLineFixed = "";
	// public String sepNew = "";

	public Hashtable<Long, Thread.State> situation = new Hashtable<Long, Thread.State>();
	public Hashtable<Long, String> position = new Hashtable<Long, String>();

	public StringBuffer readBody = new StringBuffer();
	public Set<Long> workersList = new HashSet<Long>();

	public Hashtable<String, String> firstPathDecode = new Hashtable<String, String>();
	public Hashtable<String, String> otherWord = new Hashtable<String, String>();
	public Hashtable<String, String> incomingWord = new Hashtable<String, String>();
	public Hashtable<String, String> incomingOriginalWord = new Hashtable<String, String>();
	public Hashtable<String, String> incomingWordDecode = new Hashtable<String, String>();
	public Hashtable<String, String> outgoingWord = new Hashtable<String, String>();

	public static boolean evaluate = false;
	// public static final int normal = 200;
	public static final String errorMsgEnd = "Not Found";
	public static final String zeroOne = "HTTP/1.2";
	public static final String serverPort = Integer.toString(HttpServer.portNo);
	// Socket client = null;
	public FileInputStream fs = null;

	public ArrayList<String> incomingOriginal = new ArrayList<String>();
	public ArrayList<String> otherOriginal = new ArrayList<String>();
	public ArrayList<String> bodyOriginal = new ArrayList<String>();

	// public ApplicationRegister applicationRegister =
	// ApplicationRegister.appCase("applicationregister");
	public void run() {
		workerStatus = true;
		while (workerStatus) {
			Socket client = null;
			try {
				incomingWordDecode.clear();
				incomingOriginal.clear();
				otherOriginal.clear();
				otherWord.clear();
				firstPathDecode.clear();
				incomingWord.clear();
				incomingOriginalWord.clear();

				client = (Socket) retMbq().remove();
				client.setSoTimeout(15000);

				InputStreamReader isr = new InputStreamReader(client.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				PrintStream ps = new PrintStream(client.getOutputStream(), true);

				parseIncoming(br);

				String firstRow = (String) firstPathDecode.get("path");
				setUri(firstRow);
				showStructure(firstRow);

				evaluate = evaluateLine(firstRow);
				if (evaluate) {
					int errorNum = errorNumEvaluate(ps);
					if (errorNum == 200) {
						myRequestDispatcher(ps);
					} else {
						sendReplyError(ps, errorNum, "");
					}
				} else {
					System.out.println("Insecure Access (path) 400 Bad Request\n");
					encounterError(ps, "400");
				}
				// throw new IOException();
			} catch (IOException e) {
				ApplicationLog.addToError(e.toString());
			} catch (InterruptedException e) {
				ApplicationLog.addToError(e.toString());
			} catch (NullPointerException e) {
				ApplicationLog.addToError(e.toString());
			} catch (ParseException e) {
				ApplicationLog.addToError(e.toString());
			}
		}
	}

	public void myRequestDispatcher(PrintStream ps) throws ParseException, IOException {
		String extensionDesign = extensionDesign();
		String sep = "\\?";
		String sep2 = "&";
		String sep3 = "=";
		if (extensionDesign == null) {
			sendReply(ps);
		} else {
			MyResponseContainer container = new MyResponseContainer();
			MyHttpServletRequest httpIncoming = new MyHttpServletRequest(firstPathDecode, otherWord, container);
			MyHttpServletResponse httpOutgoing = new MyHttpServletResponse(container);
			String incomingType = firstPathDecode.get("GET");
			httpIncoming.setMethod(incomingType);

			if (incomingType.equalsIgnoreCase("POST")) {
				String ask = "";
				if (otherWord.get("Content-length") != null) {
					System.out.println("Content Length: " + bodyOriginal);
					ask = bodyOriginal.get(0);
				} else {
					String line = firstPathDecode.get("path");
					String[] lineSep = line.split(sep);
					if (lineSep.length == 2) {
						ask = lineSep[1];
					}
				}

				if (ask != null) {
					String[] askList = ask.split(sep2);
					for (int key = 0; key < askList.length; key++) {
						String[] cost = askList[key].split(sep3);
						if (cost.length == 2) {
							httpIncoming.setParameter(cost[0], cost[1]);
						}
					}
				}
			}

			String neededExtension = extensionDesign;
			System.out.println(neededExtension);
			System.out.println(HttpServer.existingExtensions());
			HttpServlet newExtension = HttpServer.existingExtensions().get(neededExtension);
			try {
				// MyHttpSession instance = (MyHttpSession)
				// httpIncoming.getSession();
				System.out.println(newExtension);
				newExtension.service(httpIncoming, httpOutgoing);
				httpOutgoing.flushBuffer();
				ps.print(container.emptyReply());
				ps.flush();
				ps.close();
			} catch (ServletException e) {
				// applicationregister.updateRegister(e.toString());
				ApplicationLog.addToError(e.toString());
			} catch (IOException e) {
				// applicationregister.updateRegister(e.toString());
				ApplicationLog.addToError(e.toString());
			}
		}
	}

	public String extensionDesign() {
		String sep = "/";
		String sep2 = "\\?";
		String line = firstPathDecode.get("path");
		String[] lineSep = line.split(sep);
		if (lineSep.length == 0 || lineSep.length == 1) {
			return null;
		} else {
			String[] separatedUrl = lineSep[1].split(sep2);
			String name = separatedUrl[0];
			String kind = sep + separatedUrl[0];
			HashMap<String, String> extensionsAddress = HttpServer.returnExtensionAddress();
			for (String index : extensionsAddress.keySet()) {
				String[] indexSep = index.split(sep);
				if (indexSep.length == 3) {
					if (indexSep[1].equalsIgnoreCase(name)) {
						return extensionsAddress.get(index);
					}
				}
			}
			if (extensionsAddress.get(kind) != null) {
				return extensionsAddress.get(kind);
			} else {
				return null;
			}
		}
	}

	public byte[] destinationStructure(String initialLine) throws IOException {
		File fTemp = new File(url + initialLine);
		byte[] bTemp = new byte[(int) fTemp.length()];
		fs = new FileInputStream(fTemp);
		fs.read(bTemp);
		fs.close();
		return bTemp;
	}

	public void sendReplyError(PrintStream ps, int errorNum, String reason) {
		if (errorNum == 400) {
			System.out.println("Host Header is not received: " + errorNum + " " + errorMsgEnd + "\n");
			encounterError(ps, Integer.toString(errorNum));
		}
		if (errorNum == 404) {
			System.out.println("Absolute path is not supported by HTTP: " + errorNum + " " + errorMsgEnd + "\n");
			encounterError(ps, Integer.toString(errorNum));
		}
	}

	public void decodeFirstPath(ArrayList<String> firstPath) {
		int token = 0;
		ArrayList<String> type = new ArrayList<String>();
		type.addAll(Arrays.asList("GET", "path", "HTTP"));
		// String iterator = null;
		for (String iterator : firstPath) {
			if (token != 1) {
				firstPathDecode.put(type.get(token), iterator.toUpperCase());
				incomingWord.put(type.get(token), iterator.toUpperCase());
				incomingOriginalWord.put(type.get(token), iterator.toUpperCase());
			} else {
				firstPathDecode.put(type.get(token), iterator);
				incomingWord.put(type.get(token), iterator);
				incomingOriginalWord.put(type.get(token), iterator);
			}
			token++;
		}

		String path = firstPathDecode.get("path");
		String httpType = firstPathDecode.get("HTTP");

		if (path.contains("http://")) {
			if (httpType.equalsIgnoreCase(zeroOne)) {
				try {
					URL absolute = new URL(path);
					String absPath = absolute.getPath().toLowerCase();
					String absHost = absolute.getHost();
					String absPort = Integer.toString(absolute.getPort());

					firstPathDecode.remove("path");
					incomingWord.remove("path");
					firstPathDecode.put("path", absPath);
					incomingWord.put("path", absPath);
					otherWord.put("Host", absHost);
					incomingWord.put("Host", absHost);
					otherWord.put("Port", absPort);
					incomingWord.put("Port", absPort);
				} catch (Exception e) {
					// System.out.println("Error: " + e);
					// applicationRegister.updateRegister(e.toString());
					ApplicationLog.addToError(e.toString());
				}
			}
		}
	}

	public void decodeIncoming(BufferedReader br) {
		boolean flag = true;
		boolean yes = false;
		String content = null;
		int contentLen = 0;
		int token = 1;
		String series = null;
		String comparision = "";
		char[] buf = null;
		boolean chunkedEncodingFlag = false;

		while (flag) {
			try {
				content = br.readLine();

				if (content.toUpperCase().contains("POST")) {
					yes = true;
				}

				if (content.contains("Content-length")) {
					series = content;
					String seriesString = series.split(":")[1].trim();
					contentLen = Integer.parseInt(seriesString);
				}

				if (content.contains("Transfer-Encoding")) {
					series = content;
					String seriesString = series.split(":")[1].trim();
					contentLen = Integer.parseInt(seriesString);
					chunkedEncodingFlag = true;
				}

				if (content == null || content.equals(comparision)) {
					if (!yes) {
						break;
					} else {
						buf = new char[contentLen];
						br.read(buf);
						bodyOriginal.add(String.copyValueOf(buf));
						break;
					}
				}
				incomingOriginal.add(content);
			} catch (IOException e) {
				// applicationRegister.updateRegister(e.toString());
				ApplicationLog.addToError(e.toString());
			}
		}
		// System.out.println("End of headers read\n");

		// if (contentLen > 0) {
		// while (readBody.length() != contentLen) {
		// try {
		// readBody.append(br.read());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// // e.printStackTrace();
		// ApplicationLog.addToError(e.toString());
		// }
		// }
		// } else {
		// chunked encoding
		if (chunkedEncodingFlag) {
			ChunkedEncoding c = new ChunkedEncoding();
			try {
				bodyOriginal.add(c.receiveInput(br));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				ApplicationLog.addToError(e.toString());
			}
		}
		// }

		if (incomingOriginal.isEmpty()) {

		} else {
			firstLineFixed = new String(incomingOriginal.get(0));
		}

		int requestLength = incomingOriginal.size();
		for (token = 1; token < requestLength; token++) {
			otherOriginal.add(incomingOriginal.get(token));
		}
	}

	public void parseIncoming(BufferedReader br) throws IOException {
		decodeIncoming(br);
		decodeFirstPath(stringSeparator(firstLineFixed));
		if (otherOriginal.isEmpty()) {

		} else {
			decodeOtherOriginal(otherOriginal);
		}
	}

	public int errorNumEvaluate(PrintStream ps) {
		String httpVersion = incomingWord.get("HTTP");
		String hostName = incomingWord.get("Host");
		String line = incomingOriginalWord.get("path");
		int errorCode = 200;
		String http11 = "HTTP/1.1";
		String http10 = "HTTP/1.0";

		if (httpVersion.equalsIgnoreCase(http11)) {
			if (line.contains("http://")) {
				errorCode = 404;
				System.out.println(
						"Absolute path is not supported by HTTP version 1.0: " + errorCode + " " + errorMsgEnd + "\n");
				return errorCode;
			}
			if (hostName == null) {
				errorCode = 400;
				System.out.println("Host Header is not received: " + errorCode + " " + errorMsgEnd + "\n");
				return errorCode;
			} else if (!hostName.equals(("localhost")) && !hostName.equals(("localhost:" + serverPort))) {
				errorCode = 400;
				System.out.println("Invalid Host: " + errorCode + " " + errorMsgEnd + "\n");
				return errorCode;
			}
		} else if (httpVersion.equalsIgnoreCase(http10)) {
			if (line.contains("http://")) {
				errorCode = 404;
				System.out.println(
						"Absolute path is not supported by HTTP version 1.0: " + errorCode + " " + errorMsgEnd + "\n");
				return errorCode;
			}
		}
		// return normal;
		return errorCode;
	}

	public boolean evaluateLine(String str) {

		Stack<String> strHeap = new Stack<String>();
		String separator = "/";
		String temp = "..";
		ArrayList<String> strRecord = new ArrayList<String>(Arrays.asList(str.split(separator)));
		// String strIterator = "";
		boolean empty = false;
		boolean notEmpty = true;

		for (String strIterator : strRecord) {
			if (!strIterator.equals(temp)) {
				strHeap.push(strIterator);
			} else {
				if (strHeap.empty()) {
					throw new EmptyStackException();
				} else {
					strHeap.pop();
					if (strHeap.empty()) {
						return empty;
					}
				}
			}
		}
		return notEmpty;
	}

	public void createTransferReply(PrintStream ps, String initialLine) {
		try {
			String headerContent = composeReply();
			String str1 = "304";
			String str2 = "412";
			String path = "";

			// if(initialLine.equalsIgnoreCase("/control")){
			// controlApplicationReply(ps);
			// }
			// else if(initialLine.equalsIgnoreCase("/shutdown")){
			// ps.println("<html><body><h2>Archith Shivanagere Muralinath's
			// Server.</h2><h2>PennKey: archith</h2><br><br><h3>Control Panel:
			// </h3><p><a href=\"" + "shutdown" + "\"" + ">" + "Shutdown
			// Application" + "</a></p></body></html>");
			// closeApplications();
			// }
			if (parseUri(initialLine) && !headerContent.contains(str1) && !headerContent.contains(str2)) {
				path = url + initialLine;
				File isDir = new File(path);
				if (!isDir.isDirectory()) {
					byte[] response = destinationStructure(initialLine);
					ps.print("Content-length:" + response.length + "\r\n");
					ps.print("\r\n");
					ps.write(response);
					ps.flush();
					ps.close();
				} else {
					dirStructure(ps, initialLine);
				}
			} else {
				ps.flush();
				ps.close();
			}
			// throw new Exception();
		} catch (Exception e) {
			// System.out.println("Error: " + e);
			// applicationRegister.updateRegister(e.toString());
			ApplicationLog.addToError(e.toString());
		}
	}

	public String composeReply() {
		String expect = incomingWordDecode.get("expect");
		String modify = incomingWordDecode.get("modify");
		String http = incomingWordDecode.get("HTTP");
		String date = incomingWordDecode.get("Date");
		String contentType = incomingWordDecode.get("Content-Type");
		String completeReply = "";
		String hundred = "100-continue";
		String threeZeroFour = "304 Not Modified";
		String fourZeroOne = "412 Precondition Failed";
		String httpVersion10 = "HTTP/1.0";
		String httpVersion11 = "HTTP/1.1";
		String ct = "Content-Type:";
		String contentEnd = "Connection: close";

		if (expect != null) {
			if (expect.equalsIgnoreCase(hundred)) {
				completeReply = completeReply + httpVersion11 + " 100 Continue" + "\r\n\n";
			}
		}
		if (modify != null) {
			if (modify.equalsIgnoreCase(threeZeroFour)) {
				completeReply = completeReply + httpVersion11 + " 304 Not Modified" + "\r\n" + date + "\r\n"
						+ contentEnd + "\r\n\n";
				return completeReply;
			}
			if (modify.equalsIgnoreCase(fourZeroOne)) {
				completeReply = completeReply + httpVersion11 + " 412 Precondition Failed" + "\r\n" + date + "\r\n"
						+ contentEnd + "\r\n\n";
				return completeReply;
			} else if (modify.equalsIgnoreCase("true")) {

			} else
				System.out.println("Unknown Error.");
		}

		if (http.equalsIgnoreCase(httpVersion10)) {
			// send 1.1 or 1.0?
			completeReply = completeReply + httpVersion10 + " 200 OK\r\n";
			if (date != null) {
				completeReply = completeReply + "Date: " + date + "\r\n";
			}
			// completeReply = completeReply + ct + contentType + "\r\n";
		} else if (http.equalsIgnoreCase(httpVersion11)) {
			completeReply = completeReply + httpVersion11 + " 200 OK\r\n";
			if (date != null) {
				completeReply = completeReply + "Date: " + date + "\r\n";
			}
			// completeReply = completeReply + ct + contentType + "\r\n";
		} else {

		}
		completeReply = completeReply + ct + contentType + "\r\n";
		completeReply = completeReply + contentEnd + "\r\n";
		return completeReply;
	}

	public boolean parseUri(String initialLine) {
		File content = new File(url + initialLine);
		if (!content.exists()) {
			return false;
		}
		return true;
	}

	public void decodeOtherOriginal(ArrayList<String> other) {
		// String iterator = null;
		for (String iterator : other) {
			decodeEach(iterator);
		}
	}

	public ArrayList<String> stringSeparator(String temp) {
		String[] separate = temp.split("\\s+");
		ArrayList<String> combine = new ArrayList<String>(Arrays.asList(separate));
		return combine;
	}

	public ArrayList<String> stringSeparatorOther(String temp) {
		String[] separate = temp.split(":");
		ArrayList<String> combine = new ArrayList<String>();
		int noOfElements = separate.length;
		String endString = "";
		// String iterator = "";
		int token = 2;
		String otherItems = "";

		// if(noOfElements == 0){
		// return combine;
		// }
		if (noOfElements == 1) {
			separate[0] = separate[0].trim();
			combine.add(separate[0].trim());
			combine.add(endString);
		} else if (noOfElements == 2) {
			for (String iterator : separate) {
				combine.add(iterator.trim());
			}
		} else if (noOfElements >= 2) {
			combine.add(separate[0]);
			otherItems = separate[1];
			for (token = 2; token < noOfElements; token++) {
				otherItems = otherItems + ":" + separate[token];
			}
			combine.add(otherItems.trim());
		}
		return combine;
	}

	public boolean showStructure(String initialLine) {
		// ArrayList<String> supportedFormats = new ArrayList<String>();
		// supportedFormats.addAll(Arrays.asList(".gif", ".html", ".jpg",
		// ".png", ".txt"));
		String mime = "";
		// if(!initialLine.contains(".")){
		// incomingWordDecode.put("Content-Type", "text/html");
		// }
		Path line = Paths.get(url + initialLine);
		String isDir = "directory";
		// else{
		// for(String iterator : supportedFormats){
		// if(initialLine.contains(iterator)){
		// if(iterator.equalsIgnoreCase(".gif")){
		// incomingWordDecode.put("Content-Type", "image/gif");
		// }
		// else if(iterator.equalsIgnoreCase(".html")){
		// incomingWordDecode.put("Content-Type", "text/html");
		// }
		// else if(iterator.equalsIgnoreCase(".jpg")){
		// incomingWordDecode.put("Content-Type", "image/jpeg");
		// }
		// else if(iterator.equalsIgnoreCase(".png")){
		// incomingWordDecode.put("Content-Type", "image/png");
		// }
		// else if(iterator.equalsIgnoreCase(".txt")){
		// incomingWordDecode.put("Content-Type", "text/plain");
		// }
		// }
		// }
		// }
		try {
			mime = Files.probeContentType(line);
		} catch (Exception e) {
			// applicationRegister.updateRegister(e.toString());
			ApplicationLog.addToError(e.toString());
		}
		if (mime == null) {
			incomingWordDecode.put("Content-Type", "text/html");
		} else {
			if (mime.contains(isDir)) {
				incomingWordDecode.put("Content-Type", "text/html");
			} else {
				incomingWordDecode.put("Content-Type", mime);
			}
		}
		return true;
	}

	public void decodeEach(String other) {
		ArrayList<String> separate = stringSeparatorOther(other);
		otherWord.put(separate.get(0), separate.get(1));
		incomingWord.put(separate.get(0), separate.get(1));
		incomingOriginalWord.put(separate.get(0), separate.get(1));
	}

	public void changeStatus(boolean workerStatus) {
		this.workerStatus = workerStatus;
	}

	public static void filesFolderLocation(String rootDir) {
		url = rootDir;
	}

	public String workerCondition() {
		return uri;
	}

	public Worker(MyBlockingQueue mbq) {
		Worker.currentThread(mbq);
	}

	public static void currentThread(MyBlockingQueue mbq) {
		Worker.mbq = mbq;
	}

	public void setUri(String initialLine) {
		uri = initialLine;
	}

	public void closeApplications() throws IOException {
		// MyThreadPool.destroyConnection();
		HttpServer.serverShutdown();
	}

	public static MyBlockingQueue retMbq() {
		return mbq;
	}

	public void sendReply(PrintStream ps) throws ParseException, IOException {
		String response = (String) firstPathDecode.get("path");
		File content = new File(url + response);
		Parser.compose(incomingWordDecode, incomingWord, content);
		String headerContent = composeReply();
		ps.print(headerContent);
		String details = incomingWord.get("GET");
		String h = "HEAD";
		String g = "GET";
		int errorCode = 200;

		if (details.equalsIgnoreCase(g)) {

		} else if (details.equalsIgnoreCase(h)) {
			ps.flush();
			ps.close();
		} else {
			System.out.println("Error");
		}

		if (!details.equalsIgnoreCase(h)) {
			Parser.compose(incomingWordDecode, incomingWord, content);
			if (response.equalsIgnoreCase("/control")) {
				controlApplicationReply(ps);
			} else if (response.equalsIgnoreCase("/shutdown")) {
				// ps.println("<html><body><h2>Archith Shivanagere Muralinath's
				// Server.</h2><h2>PennKey: archith</h2><br><br><h3>Control
				// Panel: </h3><p><a href=\"" + "shutdown" + "\"" + ">" +
				// "Application Shutdown" + "</a></p></body></html>");
				// closeApplications();
				shutdownPage(ps);
			} else if (parseUri(response)) {
				if (showStructure(response)) {
					createTransferReply(ps, response);
				} else {
					errorCode = 400;
					System.out.println("File Format Not Supported: 400 Not Found\n");
					encounterError(ps, Integer.toString(errorCode));
				}
			} else {
				errorCode = 404;
				encounterError(ps, Integer.toString(errorCode));
			}
		}
	}

	// method for chunked encoding
	public StringBuilder extraCredit(StringBuilder content, int chunk) {
		StringBuilder encode = new StringBuilder();
		int size = content.length();
		// int encodeSize = encode.length();
		int zero = 0;
		String sep = ";";
		String waste = "useless";

		while (size > zero) {
			size = content.length();
			if (size < chunk) {
				size = content.length();
				if (encode.length() == zero) {
					encode.append(Integer.toHexString(size) + sep + waste + "\r\n");
				} else {
					encode.append(Integer.toHexString(size) + "\r\n");
				}
				size = content.length();
				encode.append(content.substring(zero, size) + "\r\n");
				content.delete(zero, size);
			} else {
				if (encode.length() == zero) {
					encode.append(Integer.toHexString(chunk) + sep + waste + "\r\n");
				} else {
					encode.append(Integer.toHexString(chunk) + "\r\n");
				}
				encode.append(content.substring(zero, chunk) + "\r\n");
				content.delete(zero, chunk);
			}
		}
		encode.append(zero);
		encode.append("\r\n");
		return encode;
	}

	public void encounterError(PrintStream ps, String code) {
		if (code.equalsIgnoreCase("400")) {
			ps.print("HTTP/1.1 400 Bad Request\r\n");
			ps.flush();
			ps.close();
		} else if (code.equalsIgnoreCase("404")) {
			ps.print("HTTP/1.1 404 Not Found\r\n");
			ps.flush();
			ps.close();
		} else if (code.equalsIgnoreCase("415")) {
			ps.print("HTTP/1.1 415 Unsupported File Type\r\n");
			ps.flush();
			ps.close();
		} else if (code.equalsIgnoreCase("304")) {
			ps.print("HTTP/1.1 304 Not Modified\r\n");
			ps.flush();
			ps.close();
		} else if (code.equalsIgnoreCase("401")) {
			ps.print("HTTP/1.1 401 Not Authorized\r\n");
			ps.flush();
			ps.close();
		} else if (code.equalsIgnoreCase("404")) {
			ps.print("HTTP/1.1 505 Unknown Error\r\n");
			ps.flush();
			ps.close();
		}
	}

	public void dirStructure(PrintStream ps, String initialLine) {
		File presentDirectory = new File(url + initialLine);
		File[] folderContent = presentDirectory.listFiles();
		StringBuilder content = new StringBuilder();
		String location = null;
		String shortLocation = null;
		String encode = "Transfer-Encoding: chunked";
		String addUri = "/";
		int num = 26;
		String pattern = "/+";

		content.append(
				"<html><body><h2>Archith Shivanagere Muralinath's Server.</h2><h2>PennKey: archith</h2><br><br><h3>Directory Listing</h3>");

		for (File temp : folderContent) {
			if (!temp.isHidden()) {
				location = initialLine + addUri + temp.getName();
				shortLocation = location.trim().replaceAll(pattern, addUri);
				content.append("<p><a href=\"" + shortLocation + "\"" + ">" + temp.getName() + "</a></p>");
			}
		}
		content.append("</body></html>");
		if (firstPathDecode.get("HTTP").equalsIgnoreCase("HTTP/1.0")) {
			ps.println("Content-length: " + content.length());
			ps.println("\r\n");
			ps.println(content.toString());
		} else {
			ps.println(encode);
			ps.println("\r\n");
			ps.println(extraCredit(content, num));
		}
		ps.flush();
		ps.close();
	}

	public void shutdownPage(PrintStream ps) throws IOException {
		StringBuilder response = new StringBuilder();
		String encode = "Transfer-Encoding: chunked";
		int num = 26;
		response.append("<html><body><h2>Archith Shivanagere Muralinath's Server.</h2>\n"
				+ "<h2>PennKey: archith</h2>\n" + "<br><br><h3>Control Panel: </h3><p><a href=\"" + "shutdown" + "\""
				+ ">" + "Do you want to verify successful server shutdown?" + "</a></p></body></html>");
		if (firstPathDecode.get("HTTP").equalsIgnoreCase("HTTP/1.0")) {
			ps.println("Content-length: " + response.length());
			ps.println("\r\n");
			ps.println(response.toString());
		} else {
			ps.println(encode);
			ps.println("\r\n");
			ps.println(extraCredit(response, num));
		}
		ps.flush();
		ps.close();
		closeApplications();
	}

	public void controlApplicationReply(PrintStream ps) {
		StringBuilder content = new StringBuilder();
		situation = HttpServer.situation();
		position = HttpServer.position();
		workersList = HttpServer.workersList();
		String workerActive = null;
		String encode = "Transfer-Encoding: chunked";
		int num = 26;
		content.append(
				"<html><body><h2>Archith Shivanagere Muralinath's Server.</h2><h2>PennKey: archith</h2><br><br><h3>Control Panel: </h3>");
		for (Long iterator : workersList) {
			if (situation.get(iterator) != Thread.State.WAITING) {
				workerActive = position.get(iterator);
			} else {
				workerActive = situation.get(iterator).toString();
			}
			content.append("<h3>" + Long.toString(iterator) + "<a href=\"" + workerActive + "\"" + ">" + workerActive
					+ "</a></h3>");
		}
		content.append("<p><a href=\"" + "shutdown" + "\"" + ">" + "Shutdown" + "</a></p>"
				+ "<p>Please find the server log below.</p>" + "</body></html>");
		content.append(ApplicationLog.errorBuf);
		if (firstPathDecode.get("HTTP").equalsIgnoreCase("HTTP/1.0")) {
			ps.println("Content-length: " + content.length());
			ps.println("\r\n");
			ps.println(content.toString());
		} else {
			ps.println(encode);
			ps.println("\r\n");
			ps.println(extraCredit(content, num));
		}
		// System.out.println("Flush");
		// ps.println(ApplicationLog.getErrorFile());
		// System.out.println("GET ERROR BUF " + ApplicationLog.errorBuf);
		ps.flush();
		ps.close();
	}
}
