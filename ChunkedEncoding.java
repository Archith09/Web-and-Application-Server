package edu.upenn.cis455.webserver;

import java.io.BufferedReader;

public class ChunkedEncoding {

	// complete
	public String receiveInput(BufferedReader inputFromClient) throws Exception {
		int i;
		StringBuffer str1 = new StringBuffer();
		StringBuffer str2 = new StringBuffer();
		boolean transferEncodingEnd = false;

		/*
		 * 4\r\n Wiki\r\n 5\r\n pedia\r\n E\r\n in\r\n \r\n chunks.\r\n 0\r\n
		 * \r\n
		 */
		

		while (!transferEncodingEnd && (i = inputFromClient.read()) != -1)// read
		// character
		{
			// transfer encoding stops when 0 is received followed by 2 back to
			// back CRLF
			str1.append((char) i);

			if (str1.toString().matches("\\r\\n")) {
				transferEncodingEnd = true;
				break;
			}

			if (str1.toString().matches(".*\\r\\n$")) {
				int chunkLength = Integer
						.parseInt((str1.toString().trim()), 16); // hex to
																	// integer


				while (chunkLength > 0) {
					// extract the next words as body
					i = inputFromClient.read();
					str2.append((char) i);
					chunkLength = chunkLength - 1;
				}

				str1 = str1.delete(0, str1.length());

				// next 2 characters must be \\r\\n | \\n
				i = inputFromClient.read();
				str1.append((char) i);
				i = inputFromClient.read();
				str1.append((char) i);

				if (str1.toString().matches("\\r\\n")) {
					str1 = str1.delete(0, str1.length());
					continue;
				} else
					throw new Exception("Error in format");
			}

		}
		//System.out.println("FINAL ANSWER: " + str2.toString());
		return str2.toString();
	}

	public String sendResponse(StringBuffer body, int chunkSize) {
		StringBuffer output = new StringBuffer();
		
		while(body.length() >= chunkSize){
			
			// create an output
			output.append(chunkSize);
			output.append("\r\n");
			output.append(body.substring(0, chunkSize));
			output.append("\r\n");
			
			// update the input body
			body = body.delete(0, chunkSize);
		}
		boolean endFlag = false;
		if(body.length() == 0)
			endFlag = true;
		
		output.append(body.length());
		output.append("\r\n");
		
		
		if(endFlag){
			output.append("\r\n");
			
		}else{
			output.append(body);
			output.append("\r\n");
		
			body = body.delete(0, body.length());
			
			output.append(body.length()); // 0
			output.append("\r\n");
			output.append("\r\n");
		}
		
		System.out.println("Output response:\n" + output.toString());
		return output.toString();
	}

}