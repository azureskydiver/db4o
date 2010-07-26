/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import javax.jdo.*;

import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.versant.event.*;
import com.versant.odbms.*;
import com.versant.odbms.model.*;

import db4ounit.*;

public class VodEventTestCase extends VodEventTestCaseBase {
	
	
	
	// Doesn't work: VOD throws ClassCastException when
	// trying to get system schema classes.
	public void _testEventSchemaCreation(){
		
		// The following now happens in classSetup() anyway, so we don't have to do it here.
		// _vod.createEventSchema();
		
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
		VodEventDriver eventDriver = new VodEventDriver(newEventConfiguration());
		Assert.isTrue(eventDriver.start());
		eventDriver.stop();
	}

	
	public void testSimpleEvent() throws Exception {
		PersistenceManager pm = _vod.createPersistenceManager();
		VodEventDriver eventDriver = startEventDriver();
		try{
			EventClient client = EventProcessor.newEventClient(newEventConfiguration());
			try{
				
			    ExceptionListener exception_listener = new ExceptionListener (){
			        public void exceptionOccurred (Throwable exception){
			            exception.printStackTrace ();
			        }
			    };
			    
			    client.addExceptionListener (exception_listener);
		
			    EventChannel channel = client.getChannel ("item");
			    if (channel == null) {
			        ClassChannelBuilder builder = new ClassChannelBuilder (_jdo.schemaName(Item.class));
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
			}
		} finally{
			eventDriver.stop();
		}
	}
	


}
