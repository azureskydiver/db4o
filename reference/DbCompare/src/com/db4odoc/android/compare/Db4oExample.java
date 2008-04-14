/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.android.compare;

import java.io.File;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.query.Predicate;
import com.db4o.query.Query;


public class Db4oExample {
	
	private static ObjectContainer _container = null;
	private static Context _context = null;
	private static TextView _console = null;
	
	
	public static void init(Context context, TextView console){
		_context = context;
		_console = console;
	}
	// end init

	public static void init(Context context){
		_context = context;
	}
	// end init

	
	public static ObjectContainer database(){
		long startTime = 0;
    	try {
    		if(_container == null){
    			startTime = System.currentTimeMillis();
    			_container = Db4o.openFile(configure(), db4oDBFullPath());
    		}
    	} catch (Exception e) {
        	Log.e(Db4oExample.class.getName(), e.toString());
        	return null;
        }
    	logToConsole(startTime, "Database opened: ", false);
    	return _container;
    }
	// end database
	
	private static void logToConsole(long startTime, String message, boolean add) {
		if (_console != null){
			long diff = 0;
    		if (startTime != 0){
    			diff = (System.currentTimeMillis() - startTime);
    		} 
			if (add){
				_console.setText(_console.getText() + "\n" + message + diff + " ms.");
			} else {
				_console.setText("db4o: " + message + diff + " ms.");
			}
    	}
	}
	// end database
	
	private static Configuration configure(){
		Configuration configuration = Db4o.newConfiguration();
    	configuration.objectClass(Car.class).objectField("pilot").indexed(true);
    	configuration.objectClass(Pilot.class).objectField("points").indexed(true);
        
    	return configuration;
    }
	
	private static String db4oDBFullPath() throws Exception {
		if (_context == null){
			throw new Exception("Db4o Module not initialized");
		}
		return _context.getDataDir() + "/" + "android.db4o";
	}
	
	/**
     * Close database connection
     */
    public static void close() {
    	if(_container != null){
    		long startTime = System.currentTimeMillis();
    		_container.close();
    		logToConsole(startTime, "Database committed and closed: ", false);
    		_container = null;
    	}
    }
	
	
    public static void fillUpDB() throws Exception {
    	close();
        new File(db4oDBFullPath()).delete();
        ObjectContainer container=database();
        if (container != null){
        	long startTime = System.currentTimeMillis();
        	for (int i=0; i<100;i++){
    			AddCar(container,i);
    		}
        	logToConsole(startTime, "Stored 100 objects: ", false);
        	startTime = System.currentTimeMillis();
        	container.commit();
        	logToConsole(startTime, "Committed: ", true);
		}
    }
    // end fillUpDB
  
    public static void updateCar(){
        ObjectContainer container=database();
        if (container != null){
	        try {
	        	long startTime = System.currentTimeMillis();
	        	ObjectSet result = container.query(new Predicate(){
	        		public boolean match(Object object){
	        			if (object instanceof Car){
	        				return ((Car)object).getPilot().getPoints() == 15;
	        			}
	        			return false;
	        		}
	        	});
	        	Car car = (Car)result.next();
	        	car.setPilot(new Pilot("Tester1", 25));
	        	container.set(car);
	        	logToConsole(startTime, "Updated selected object: ", false);
			} catch (Exception e){
				logToConsole(0, "Car not found, refill the database to continue.", false);
			}
        }
    }
    // end updateCar
  
    public static void deleteCar(){
        ObjectContainer container=database();
        if (container != null){
	        try {
	        	long startTime = System.currentTimeMillis();
	        	ObjectSet result = container.query(new Predicate(){
	        		public boolean match(Object object){
	        			if (object instanceof Car){
	        				return ((Car)object).getPilot().getPoints() == 5;
	        			}
	        			return false;
	        		}
	        	});
	        	Car car = (Car)result.next();
	        	container.delete(car);
	        	logToConsole(startTime, "Deleted selected object: ", false);
			} catch (Exception e){
				logToConsole(0, "Car not found, refill the database to continue.", false);
			}
        }
    }
    // end deleteCar
  
    public static void backup(){
    	try {
    		new File(db4oDBFullPath() + ".bak").delete();
            ObjectContainer container=database();
            if (container != null){
        		long startTime = System.currentTimeMillis();
        		container.ext().backup(db4oDBFullPath() + ".bak");
        		logToConsole(startTime, "Backed up to android.db4o.bak: ", false);
            }	
    	} catch (Exception e){
    		logToConsole(0, "Backup failed.", false);
    	}
    }
    // end backup
    
    public static void selectCar() {
    	ObjectContainer container = database();
    	if (container != null){
			Query query = container.query();
			query.constrain(Car.class);
			query.descend("pilot").descend("points").constrain(new Integer(9));

			long startTime = System.currentTimeMillis();
			ObjectSet  result = query.execute();
			if (result.size() == 0){
				logToConsole(0, "Car not found, refill the database to continue.", false);
			} else {
				logToConsole(startTime, "Selected Car (" + result.next() + "): ", false);
			}
    	}
    }
    // end selectCar

    
    private static void AddCar(ObjectContainer container, int points)
	{
		Car car = new Car("BMW");
		car.setPilot(new Pilot("Tester", points));
		container.set(car);
	}
    // end AddCar
    

    
}
