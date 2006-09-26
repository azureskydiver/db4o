/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.persist;


public class Change2 implements Runnable {
	Thread t;
	Car car;
	boolean running;
	
	public void init(Car car){
		this.car = car;
		running = true;
		t = new Thread(this);
		t.start();
	}
	
	public void stop(){
		running = false;
	}
	
	public void run() {
		while(running)
		{
			car.setTemperature(getConditionerTemperature());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}	
	}
	
	private int getConditionerTemperature(){
		return 17;
	}

}
