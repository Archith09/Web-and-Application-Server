package edu.upenn.cis455.webserver;

import java.util.*;

/*
 * class for managing sessions
 */

public class SessionController {
	private static SessionController sessionCase = null;
	static Hashtable<String, MyHttpSession> allSessions;

	public SessionController(){
		allSessions = new Hashtable<String, MyHttpSession>();
	}

	public static SessionController sessionCase(){
		if(sessionCase == null)
			sessionCase = new SessionController();
		return sessionCase;
	}

	// send back session cookie
	public MyHttpSession fetchBoth(String value){
		return allSessions.get(value);
	}

	// return requested session
	public static MyHttpSession fetchSession(String value){
		return allSessions.get(value);
	}

	// add a new session to hashtable
	public static void newSession(MyHttpSession id){
		allSessions.put(id.getId(), id);
	}
}
