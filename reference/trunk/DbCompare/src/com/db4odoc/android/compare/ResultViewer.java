package com.db4odoc.android.compare;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultViewer extends Activity {
	TextView console;
	TextView consoleDb4o;
	TextView consoleSql;
	

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
    	
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        console = (TextView) findViewById(R.id.console);
        console.setText("Compare db4o vs SQLite");
        consoleDb4o = (TextView) findViewById(R.id.db4o_console);
        consoleSql = (TextView) findViewById(R.id.sqlite_console);
        
        // Initialize database modules
        Db4oExample.init(this, consoleDb4o);
        SqlExample.init(this, consoleSql);
        
            Button openButton = (Button) findViewById(R.id.open_button);
	        
	        openButton.setOnClickListener(new View.OnClickListener() {
	
	    	    public void onClick(View arg0) {
	    	    	Db4oExample.database();
	    	    	SqlExample.database();
	    	    }
	    	});
	
	        Button storeButton = (Button) findViewById(R.id.store_button);
	        
	        storeButton.setOnClickListener(new View.OnClickListener() {
	
	    	    public void onClick(View arg0) {
	    	    	try {
	    	    		Db4oExample.fillUpDB();
	    	    		SqlExample.fillUpDB();
	    	    	} catch (Exception e){
	    	        	console.setText("Unexpected exception: " + e.getMessage());
	    	        }
	    	    }
	    	});
	
	        Button updateButton = (Button) findViewById(R.id.update_button);
	        
	        updateButton.setOnClickListener(new View.OnClickListener() {
	
	    	    public void onClick(View arg0) {
	    	    	Db4oExample.updateCar();
	    	    	SqlExample.updateCar();
	    	    }
	    	});
	
	        Button deleteButton = (Button) findViewById(R.id.delete_button);
	        
	        deleteButton.setOnClickListener(new View.OnClickListener() {
	
	    	    public void onClick(View arg0) {
	    	    	Db4oExample.deleteCar();
	    	    	SqlExample.deleteCar();
	    	    }
	    	});
	
	        Button retrieveButton = (Button) findViewById(R.id.retrieve_button);
	        
	        retrieveButton.setOnClickListener(new View.OnClickListener() {
	
	    	    public void onClick(View arg0) {
	    	    	Db4oExample.selectCar();
	    	    	SqlExample.selectCar();
	    	    }
	    	});
	
	        Button closeButton = (Button) findViewById(R.id.close_button);
	        
	        closeButton.setOnClickListener(new View.OnClickListener() {
	
	    	    public void onClick(View arg0) {
	    	    	Db4oExample.close();
	    	    	SqlExample.close();
	    	    }
	    	});
	        
	        Button backupButton = (Button) findViewById(R.id.backup_button);
	        
	        backupButton.setOnClickListener(new View.OnClickListener() {
	
	    	    public void onClick(View arg0) {
	    	    	Db4oExample.backup();
	    	    }
	    	});
    }
}