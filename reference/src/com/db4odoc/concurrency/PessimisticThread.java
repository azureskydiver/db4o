package com.db4odoc.concurrency;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;

class PessimisticThread extends Thread {
    private ObjectServer _server;
    private ObjectContainer _db;
                            
    public PessimisticThread(String id, ObjectServer server) {
        super(id);
        this._server = server;
        _db = _server.openClient();
    }
    // end PessimisticThread
    
	public void run() {
    	try {
    		ObjectSet result = _db.get(Pilot.class);
    		while (result.hasNext()){
    			Pilot pilot = (Pilot)result.next();
    			/* with pessimistic approach the object is locked as soon 
    			 * as we get it 
    			 */ 
    			if (!_db.ext().setSemaphore("LOCK_"+_db.ext().getID(pilot), 0)){
    				System.out.println("Error. The object is locked");
    			}
    			
    			System.out.println(getName() + "Updating pilot: " + pilot);
    	        pilot.addPoints(1);
    	        _db.set(pilot);
    	        /* The changes should be committed to be 
    	         * visible to the other clients
    	         */
    	        _db.commit();
    	        _db.ext().releaseSemaphore("LOCK_"+_db.ext().getID(pilot));
    	        System.out.println(getName() + "Updated pilot: " + pilot);
    	        System.out.println();
    		}
    	} finally {
    		_db.close();
    	}
    }
    // end run
}   
   