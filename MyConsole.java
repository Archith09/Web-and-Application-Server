package edu.upenn.cis455.webserver;

import java.io.*;

/*
 * class to print to MyResponseBuffer
 */

public class MyConsole extends PrintWriter {
	public MyHttpServletResponse mhsr;
	public MyResponseContainer mrb;

	public MyConsole(MyResponseContainer mrb, MyHttpServletResponse mhsr){
		super(System.out, true);
		this.mrb = mrb;
		this.mhsr = mhsr;
	}

	public void print(String content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
		}
	}

	public void print(boolean content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
		}	
	}

	public void print(char content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
		}	
	}

	public void print(int content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
		}	
	}

	public void print(long content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
		}	
	}

	public void print(float content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
		}	
	}

	public void print(double content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
		}	
	}

	public void print(char[] content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
		}	
	}

	public void println(String content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
			mrb.add("\n");
		}	
	}

	public void println(boolean content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
			mrb.add("\n");
		}	
	}

	public void println(char content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
			mrb.add("\n");
		}	
	}

	public void println(int content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
			mrb.add("\n");
		}	
	}

	public void println(long content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
			mrb.add("\n");
		}	
	}

	public void println(float content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
			mrb.add("\n");
		}	
	}

	public void println(double content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
			mrb.add("\n");
		}	
	}

	public void println(char[] content){
		if(mhsr.isCommitted()){
			throw new IllegalStateException();
		}
		else{
			mrb.add(content);
			mrb.add("\n");
		}	
	}
}
