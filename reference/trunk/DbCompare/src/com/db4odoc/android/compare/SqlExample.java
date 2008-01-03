/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.android.compare;

import java.io.FileNotFoundException;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;


public class SqlExample {
	
	private static final String DATABASE_NAME = "android";
    private static final String DB_TABLE_PILOT = "pilot";
    private static final String DB_TABLE_CAR = "car";
    private static final int DATABASE_VERSION = 1;
    
    private static SQLiteDatabase _db = null;
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

	
	public static SQLiteDatabase database(){
		long startTime = 0;
		try {
            _db = _context.openDatabase(DATABASE_NAME, null);
        } catch (FileNotFoundException e) {
            try {
                _db =
                	_context.createDatabase(DATABASE_NAME, DATABASE_VERSION, 0,
                        null);
                _db.execSQL("create table " + DB_TABLE_PILOT + " ("
                	    + "id integer primary key autoincrement, "
                        + "name text not null, "
                        + "points integer not null);");
                _db.execSQL("create table " + DB_TABLE_CAR + " ("
            	    +"id integer primary key autoincrement," +
            	    		"model text not null," +
            	    		"pilot integer not null," +
            	    		"FOREIGN KEY (pilot)" +
            	    		"REFERENCES pilot(id) on delete cascade);");
            } catch (FileNotFoundException e1) {
                _db = null;
            }
        }
    	logToConsole(startTime, "Database opened: ", false);
    	return _db;
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
				_console.setText("SQLite: " + message + diff + " ms.");
			}
    	}
	}
	// end logToConsole
	
	/**
     * Close database connection
     */
    public static void close() {
    	if(_db != null){
    		long startTime = System.currentTimeMillis();
    		_db.close();
    		logToConsole(startTime, "Database committed and closed: ", false);
    		_db = null;
    	}
    }
	
	
    public static void fillUpDB() throws Exception {
    	close();
    	_context.deleteDatabase(DATABASE_NAME);
        SQLiteDatabase db = database();
        if (db != null){
        	long startTime = System.currentTimeMillis();
        	for (int i=0; i<100;i++){
    			addCar(db,i);
    		}
        	logToConsole(startTime, "Stored 100 objects: ", false);
        	startTime = System.currentTimeMillis();
		}
    }
    // end fillUpDB
  
    public static void updateCar(){
        SQLiteDatabase db = database();
        if (db != null){
	        long startTime = System.currentTimeMillis();
	        // insert a new pilot
	        ContentValues updateValues = new ContentValues();
	        
	    	updateValues.put("id", 101);
	    	updateValues.put("name", "Tester1");
	        updateValues.put("points", 25);
	        db.insert(DB_TABLE_PILOT, null, updateValues);
	        
	        updateValues = new ContentValues();
	        
	        // update pilot in the car
	        updateValues.put("pilot", 101);
	        int count = db.update(DB_TABLE_CAR, updateValues, "pilot in (select id from pilot where points = 15)", null);
	        if (count == 0){
	        	logToConsole(0, "Car not found, refill the database to continue.", false);
	        } else {	
	        	logToConsole(startTime, "Updated selected object: ", false);
	        }
        }
    }
    // end updateCar
  
    public static void deleteCar(){
        SQLiteDatabase db = database();
        if (db != null){
        	long startTime = System.currentTimeMillis();
        	int count = db.delete(DB_TABLE_CAR, "pilot in (select id from pilot where points = 5)", null);
        	if (count == 0){
        		logToConsole(0, "Car not found, refill the database to continue.", false);
        	} else {
        		logToConsole(startTime, "Deleted selected object: ", false);
        	}
			
        }
    }
    // end deleteCar
  
    public static void backup(){
    	// is done by copying db file or executing .dump from sqlite command line
    }
    // end backup
    
    public static void selectCar() {
    	SQLiteDatabase db = database();
    	if (db != null){
    		long startTime = System.currentTimeMillis();
            Cursor c =
                db.query("select c.model, p.name, p.points from car c, pilot p where c.pilot = p.id and p.points = 9;", null);
            	//db.query("select model from car;", null);
            if (c.count() == 0) {
            	logToConsole(0, "Car not found, refill the database to continue.", false);
            	return;
            }
            c.first();
            Pilot pilot = new Pilot();
            pilot.setName(c.getString(1));
            pilot.setPoints(c.getInt(2));
           
            Car car = new Car();
            car.setModel(c.getString(0));
            car.setPilot(pilot);
            logToConsole(startTime, "Selected Car (" + car + "): ", false);
    	}
    }
    // end selectCar

    
    private static void addCar(SQLiteDatabase db, int number)
	{
    	ContentValues initialValues = new ContentValues();
        
    	initialValues.put("id", number);
    	initialValues.put("name", "Tester");
        initialValues.put("points", number);
        db.insert(DB_TABLE_PILOT, null, initialValues);
        
        initialValues = new ContentValues();
        
    	initialValues.put("model", "BMW");
        initialValues.put("pilot", number);
        db.insert(DB_TABLE_CAR, null, initialValues);
	}
    // end addCar
    

    
}
