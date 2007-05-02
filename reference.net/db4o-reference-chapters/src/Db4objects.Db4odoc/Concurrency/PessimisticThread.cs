/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Concurrency
{
    public class PessimisticThread
    {
        private IObjectServer _server;
        private IObjectContainer _container;
        private string _id;


        public PessimisticThread(string id, IObjectServer server)
        {
            _id = id;
            this._server = server;
            _container = _server.OpenClient();
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
    		IObjectSet result = _container.Get(typeof(Pilot));
    		while (result.HasNext()){
    			Pilot pilot = (Pilot)result.Next();
    			/* with pessimistic approach the object is locked as soon 
    			 * as we get it 
    			 */ 
    			if (!_container.Ext().SetSemaphore("LOCK_"+_container.Ext().GetID(pilot), 0)){
    				Console.WriteLine("Error. The object is locked");
    			}
    			
    			Console.WriteLine(Name + "Updating pilot: " + pilot);
    	        pilot.AddPoints(1);
    	        _container.Set(pilot);
    	        /* The changes should be committed to be 
    	         * visible to the other clients
    	         */
    	        _container.Commit();
    	        _container.Ext().ReleaseSemaphore("LOCK_"+_container.Ext().GetID(pilot));
    	        Console.WriteLine(Name + "Updated pilot: " + pilot);
    	        Console.WriteLine();
    		}
    	} finally {
    		_container.Close();
    	}
   
        }
        // end Run
    }
}