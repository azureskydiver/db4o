/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;
import java.util.*;

import javax.jdo.*;

import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;
import com.db4o.foundation.*;
import com.db4o.util.*;
import com.versant.event.*;
import com.versant.odbms.*;
import com.versant.odbms.model.*;

import db4ounit.*;

public class VodEventTestCase implements TestCase, ClassLevelFixtureTest {
	
	private static final String DATABASE_NAME = "VodDatabaseTestCase";
	
	private static final String LOGFILE_NAME = "DrsEventLogFile.log";
	
	public static void classSetUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.createDb();
		_vod.enhance();
		System.out.println("classSetup");
	}

	public static void classTearDown() {
		_vod.removeDb();
		System.out.println("classTearDown");
	}
	
	private static VodDatabase _vod;
	
	// Doesn't work: VOD throws ClassCastException when
	// trying to get system schema classes.
	public void _testEventSchemaCreation(){
		
		_vod.createEventSchema();
		DatastoreManager dm = _vod.createDatastoreManager();
		DatastoreInfo info = dm.getPrimaryDatastoreInfo();
		SchemaEditor editor = dm.getSchemaEditor();
		boolean systemClasses = true;
		long[] classLoids = dm.locateAllClasses(info, systemClasses);
		boolean found = false;
		for (int i = 0; i < classLoids.length; i++) {
			DatastoreSchemaClass dc = editor.findClass(classLoids[i], info);
			if("VEDChannel".equals(dc.getName())){
				found = true;
			}
		}
		dm.close();
		Assert.isTrue(found);
	}
	
	public void testEventDriverStartAndStop() throws IOException {
		_vod.createEventSchema();
		EventConfiguration eventConfiguration = 
			new EventConfiguration(DATABASE_NAME,LOGFILE_NAME,  4000, 4001);
		String configFileName = Path4.getTempFileName();
		File configFile = new File(configFileName);
		eventConfiguration.writeConfigFile(configFile);
		VodEventDriver eventDriver = new VodEventDriver(DATABASE_NAME,configFile);
		Assert.isTrue(eventDriver.start());
		eventDriver.stop();
	}
	
	public void testSimpleEvent() throws Exception {
		_vod.createEventSchema();
		
		PersistenceManager pm = _vod.createPersistenceManager();
		
		EventConfiguration eventConfiguration = new EventConfiguration(DATABASE_NAME,LOGFILE_NAME,  4000, 4001);
		String configFileName = Path4.getTempFileName();
		File configFile = new File(configFileName);
		eventConfiguration.writeConfigFile(configFile);
		VodEventDriver eventDriver = new VodEventDriver(DATABASE_NAME,configFile);
		
		Assert.isTrue(eventDriver.start());
		
		EventClient client = new EventClient("localhost", 4000, "localhost", 4001, DATABASE_NAME);
		
		try{
		    ExceptionListener exception_listener = new ExceptionListener (){
		        public void exceptionOccurred (Throwable exception){
		            exception.printStackTrace ();
		        }
		    };
		    
		    client.addExceptionListener (exception_listener);
	
		    EventChannel channel = client.getChannel ("item");
		    if (channel == null) {
		        ClassChannelBuilder builder = new ClassChannelBuilder (Item.class.getSimpleName());
		        channel = client.newChannel ("item", builder);
		    }
		    
		    RecordingEventListener eventListener = new RecordingEventListener(VodEvent.CREATED, VodEvent.MODIFIED);
	
		    channel.addVersantEventListener (eventListener);
			
		    Item item = new Item("two");
			pm.currentTransaction().begin();
			pm.makePersistent(item);
			pm.currentTransaction().commit();
			
			pm.currentTransaction().begin();
			item.name("newName");
			
			pm.currentTransaction().commit();
			pm.close();
			
			eventListener.verify(10000);
		} finally {
			client.shutdown();
			eventDriver.stop();
		}
	}

}
