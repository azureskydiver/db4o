/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Concurrency
{
    public class PessimisticThread
    {
        private IObjectServer _server;
        private IObjectContainer _db;
        private string _id;


        public PessimisticThread(string id, IObjectServer server)
        {
            _id = id;
            this._server = server;
            _db = _server.OpenClient();
        }
        // end PessimisticThread

        private string Name
        {
            get
            {
                return _id;
            }
        }
        // end Name

        public void Run()
        {
            try {
    		IObjectSet result = _db.Get(typeof(Pilot));
    		while (result.HasNext()){
    			Pilot pilot = (Pilot)result.Next();
    			/* with pessimistic approach the object is locked as soon 
    			 * as we get it 
    			 */ 
    			if (!_db.Ext().SetSemaphore("LOCK_"+_db.Ext().GetID(pilot), 0)){
    				Console.WriteLine("Error. The object is locked");
    			}
    			
    			Console.WriteLine(Name + "Updating pilot: " + pilot);
    	        pilot.AddPoints(1);
    	        _db.Set(pilot);
    	        /* The changes should be committed to be 
    	         * visible to the other clients
    	         */
    	        _db.Commit();
    	        _db.Ext().ReleaseSemaphore("LOCK_"+_db.Ext().GetID(pilot));
    	        Console.WriteLine(Name + "Updated pilot: " + pilot);
    	        Console.WriteLine();
    		}
    	} finally {
    		_db.Close();
    	}
   
        }
        // end Run
    }
}