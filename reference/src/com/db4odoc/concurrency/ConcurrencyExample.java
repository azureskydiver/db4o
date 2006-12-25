/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.concurrency;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;

public class ConcurrencyExample {

	private final static String YAPFILENAME="formula1.yap";
	private static ObjectServer _server;
	
	public static void main(String[] args) {
		connect();
		try {
			savePilots();
			modifyPilotsOptimistic();
			modifyPilotsPessimistic();			
		} finally {
			disconnect();
		}
	}
	// end main

	public static void connect(){
		if (_server == null){
			new File(YAPFILENAME).delete();
			Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
			_server =  Db4o.openServer(YAPFILENAME,0);
		}
	}
	// end connect
	
	public static void disconnect(){
		_server.close();
	}
	// end disconnect
	
	public static void savePilots(){
		ObjectContainer db = _server.openClient();
		try {
			Pilot pilot = new Pilot("Kimi Raikkonnen",0);
			db.set(pilot);
			pilot = new Pilot("David Barrichello",0);
			db.set(pilot);
			pilot = new Pilot("David Coulthard",0);
			db.set(pilot);
		} finally {
			db.close();
		}
	}
	// end savePilots
	
	public static void modifyPilotsOptimistic(){
		System.out.println("Optimistic locking example");
		// create threads for concurrent modifications
		OptimisticThread t1 = new OptimisticThread("t1: ", _server);
        OptimisticThread t2 = new OptimisticThread("t2: ", _server);
        runThreads(t1,t2);
	}
	
	public static void modifyPilotsPessimistic(){
		System.out.println();
		System.out.println("Pessimistic locking example");
		// create threads for concurrent modifications
		PessimisticThread t1 = new PessimisticThread("t1: ", _server);
		PessimisticThread t2 = new PessimisticThread("t2: ", _server);
        runThreads(t1,t2);
	}
	
	public static void runThreads(Thread t1, Thread t2){
        t1.start();
        t2.start();
        
        boolean t1IsAlive = true;
        boolean t2IsAlive = true;
        
        do {
            if(t1IsAlive && !t1.isAlive()) {
                t1IsAlive = false;
                System.out.println("t1 is dead.");
            }
            
            if(t2IsAlive && !t2.isAlive()) {
                t2IsAlive = false;
                System.out.println("t2 is dead.");
            }
        } while(t1IsAlive || t2IsAlive);
	}
	// end runThreads
}
