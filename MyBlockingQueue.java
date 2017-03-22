package edu.upenn.cis455.webserver;

// to write my own blocking queue, I referred this source: http://tutorials.jenkov.com/java-concurrency/blocking-queues.html

//import java.io.*;
//import java.net.*;
import java.util.*;
import java.net.*;

public class MyBlockingQueue {

	private int maxLength = 20;
	private int minLength = 0;
	private List<Socket> myQ = new LinkedList<Socket>();
	// public static ApplicationRegister applicationRegister = ApplicationRegister.appCase("applicationregister");

	//constructor for blocking queue
	public MyBlockingQueue(int noOfRequests) {
		this.maxLength = noOfRequests;
	}

	//add new request to blocking queue
	public synchronized void include(Socket newRequest)
	throws InterruptedException {
		while(this.myQ.size() == this.maxLength){
			wait();
		}
		if(this.myQ.size() == minLength){
			notifyAll();
		}
		this.myQ.add(newRequest);
	}

	//remove a particular request from queue after responding
	public synchronized Object remove()
	throws InterruptedException {
		while(this.myQ.size() == minLength){
			wait();
		}
		if(this.myQ.size() == this.maxLength){
			notifyAll();
		}
		return this.myQ.remove(0);
	}
}
