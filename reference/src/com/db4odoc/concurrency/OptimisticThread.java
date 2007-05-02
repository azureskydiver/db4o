/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.concurrency;

import java.util.HashMap;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;

class OptimisticThread extends Thread {
    private ObjectServer _server;
    private ObjectContainer _container;
    private boolean _updateSuccess = false;
    private HashMap _idVersions;
                            
    public OptimisticThread(String id, ObjectServer server) {
        super(id);
        this._server = server;
        _container = _server.openClient();
        registerCallbacks();
        _idVersions = new HashMap();
    }
    // end OptimisticThread
    
    private void randomWait() {
        try {
            Thread.sleep((long)(5000*Math.random()));
        } catch(InterruptedException e) {
            System.out.println("Interrupted!");
        }
    }    
    // end randomWait

	public  void registerCallbacks(){
			EventRegistry registry =  EventRegistryFactory.forObjectContainer(_container);
			// register an event handler to check collisions on update
			registry.updating().addListener(new EventListener4() {
				public void onEvent(Event4 e, EventArgs args) {
					CancellableObjectEventArgs queryArgs = ((CancellableObjectEventArgs) args);
					Object obj = queryArgs.object();
					// retrieve the object version from the database
					long currentVersion = _container.ext().getObjectInfo(obj).getVersion();
					long id = _container.ext().getID(obj);
					// get the version saved at the object retrieval
					long initialVersion = ((Long)_idVersions.get(id)).longValue(); 
					if (initialVersion != currentVersion){
						System.out.println(getName() +"Collision: ");
						System.out.println(getName() +"Stored object: version: "+ currentVersion);
						System.out.println(getName() +"New object: " + obj+ " version: "+ initialVersion);
						queryArgs.cancel();
					} else {
						_updateSuccess = true;
					}
				}
			});
	}
	// end registerCallbacks
	
    public void run() {
    	try {
    		ObjectSet result = _container.get(Pilot.class);
    		while (result.hasNext()){
    			Pilot pilot = (Pilot)result.next();
    			/* We will need to set a lock to make sure that the 
    			 * object version corresponds to the object retrieved.
    			 * (Prevent other client committing changes
    			 * at the time between object retrieval and version
    			 * retrieval )
    			 */
    			if (!_container.ext().setSemaphore("LOCK_"+_container.ext().getID(pilot), 3000)){
    	        	System.out.println("Error. The object is locked");
    	        	continue;
    	        }
    			long objVersion = _container.ext().getObjectInfo(pilot).getVersion();
    			_container.ext().refresh(pilot, Integer.MAX_VALUE);
    			_container.ext().releaseSemaphore("LOCK_"+_container.ext().getID(pilot));
    			
    			/* save object version into _idVersions collection
    			 * This will be needed to make sure that the version
    			 * originally retrieved is the same in the database 
    			 * at the time of modification
    			 */
    			long id = _container.ext().getID(pilot);
    			_idVersions.put(id, objVersion);
    			
    	        System.out.println(getName() + "Updating pilot: " + pilot+ " version: "+objVersion);
    	        pilot.addPoints(1);
    	        _updateSuccess = false;
    	        randomWait();
    	        if (!_container.ext().setSemaphore("LOCK_"+_container.ext().getID(pilot), 3000)){
    	        	System.out.println("Error. The object is locked");
    	        	continue;
    	        }
    	        _container.set(pilot);
    	        /* The changes should be committed to be 
    	         * visible to the other clients
    	         */
    	        _container.commit();
    	        _container.ext().releaseSemaphore("LOCK_"+_container.ext().getID(pilot));
    	        if (_updateSuccess){
    	        	System.out.println(getName() + "Updated pilot: " + pilot);
    	        }
    	        System.out.println();
    	        /* The object version is not valid after commit
    	         * - should be removed
    	         */
    	        _idVersions.remove(id);
    		}
	        
    	} finally {
    		_container.close();
    	}
    }
    // end run
}   


   