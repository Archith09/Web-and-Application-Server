package edu.upenn.cis455.webserver;

import java.io.IOException;

//to write my own thread pool, I referred this source: http://tutorials.jenkov.com/java-concurrency/thread-pools.html

//import java.io.*;
import java.net.*;
import java.util.*;

public class MyThreadPool {

	private MyBlockingQueue mbq = null;
	private static List<Worker> myThreads = new ArrayList<Worker>();
//    public ApplicationRegister applicationRegister = ApplicationRegister.appCase("applicationregister");
	// thread pool constructor
	public MyThreadPool(int maxThreads, int maxWorkers){
        mbq = new MyBlockingQueue(maxWorkers);
        for(int i = 1; i <= maxThreads; i++){
            myThreads.add(new Worker(mbq));
        }
        for(Worker j : myThreads){
            j.start();
        }
    }

    // add new task to the blocking queue
    public void establishConnection(Socket newItem) throws IOException{
    	try{
    		mbq.include(newItem);
    	} catch (Exception e){
    		 System.out.println("Error: " + e);
//            applicationRegister.updateRegister(e.toString());
    	}
    }

    // shutdown all threads when '/shutdown' url is issued
    public static void destroyConnection(){
    	final boolean status = false;
    	for(Worker i : myThreads){
    		i.changeStatus(status);
    		i.interrupt();
    	}
    }

    // find thread state
    public static Hashtable<Long, Thread.State> findThreadState(){
    	Hashtable<Long, Thread.State> workersState = new Hashtable<Long, Thread.State>();
    	for(Worker i : myThreads){
    		workersState.put(i.getId(), i.getState());
    	}
    	return workersState;
    }

    // find thread status
    public static Hashtable<Long, String> findThreadStatus(){
    	Hashtable<Long, String> workersState = new Hashtable<Long, String>();
    	for(Worker i : myThreads){
    		if(i.getState() == Thread.State.WAITING){
    			workersState.put(i.getId(), i.getState().toString());
    		}
    		else if(i.getState() != Thread.State.WAITING){
    			workersState.put(i.getId(), i.workerCondition());
    		}
    	}
    	return workersState;
    }
}
