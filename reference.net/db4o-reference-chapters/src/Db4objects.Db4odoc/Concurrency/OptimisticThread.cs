/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Threading;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Events;

namespace Db4objects.Db4odoc.Concurrency
{
    public class OptimisticThread
    {
        private IObjectServer _server;
        private IObjectContainer _db;
        private string _id;
        private bool _updateSuccess = false;
        private Hashtable _idVersions;



        public OptimisticThread(string id, IObjectServer server)
        {
            _id = id;
            this._server = server;
            _db = _server.OpenClient();
            RegisterCallbacks();
            _idVersions = new Hashtable();
            

        }
        // end OptimisticThread

        private string Name
        {
            get
            {
                return _id;
            }
        }
        // end Name

        private void RandomWait()
        {
            try
            {
                Random r = new Random();
                Thread.Sleep((int)(5000 * r.Next(1)));
            }
            catch (Exception e)
            {
                Console.WriteLine("Interrupted!");
            }
        }
        // end RandomWait

        private void OnUpdating(object sender, CancellableObjectEventArgs args)
        {
            Object obj = args.Object;
            // retrieve the object version from the database
            long currentVersion = _db.Ext().GetObjectInfo(obj).GetVersion();
            long id = _db.Ext().GetID(obj);
            // get the version saved at the object retrieval
            IEnumerator i = _idVersions.GetEnumerator();

            long initialVersion = (long)_idVersions[id];
            if (initialVersion != currentVersion)
            {
                Console.WriteLine(Name + "Collision: ");
                Console.WriteLine(Name + "Stored object: version: " + currentVersion);
                Console.WriteLine(Name + "New object: " + obj + " version: " + initialVersion);
                args.Cancel();
            }
            else
            {
                _updateSuccess = true;
            }
        }
        // end OnUpdating

        public void RegisterCallbacks()
        {
            IEventRegistry registry = EventRegistryFactory.ForObjectContainer(_db);
            // register an event handler to check collisions on update
            registry.Updating += new CancellableObjectEventHandler(OnUpdating);
        }
        // end RegisterCallbacks

        public void Run()
        {
            try {
    		IObjectSet result = _db.Get(typeof(Pilot));
    		while (result.HasNext()){
    			Pilot pilot = (Pilot)result.Next();
                /* We will need to set a lock to make sure that the 
    			 * object version corresponds to the object retrieved.
    			 * (Prevent other client committing changes
    			 * at the time between object retrieval and version
    			 * retrieval )
    			 */
                if (!_db.Ext().SetSemaphore("LOCK_" + _db.Ext().GetID(pilot), 3000))
                {
                    Console.WriteLine("Error. The object is locked");
                    continue;
                }
                _db.Ext().Refresh(pilot, Int32.MaxValue);
    			long objVersion = _db.Ext().GetObjectInfo(pilot).GetVersion();
                _db.Ext().ReleaseSemaphore("LOCK_" + _db.Ext().GetID(pilot));
    			/* save object version into _idVersions collection
    			 * This will be needed to make sure that the version
    			 * originally retrieved is the same in the database 
    			 * at the time of modification
    			 */
    			long id = _db.Ext().GetID(pilot);
    			_idVersions.Add(id, objVersion);
    			
    	        Console.WriteLine(Name + "Updating pilot: " + pilot+ " version: "+objVersion);
    	        pilot.AddPoints(1);
    	        _updateSuccess = false;
    	        RandomWait();
    	        if (!_db.Ext().SetSemaphore("LOCK_"+_db.Ext().GetID(pilot), 3000)){
    	        	Console.WriteLine("Error. The object is locked");
    	        	continue;
    	        }
    	        _db.Set(pilot);
    	        /* The changes should be committed to be 
    	         * visible to the other clients
    	         */
    	        _db.Commit();
    	        _db.Ext().ReleaseSemaphore("LOCK_"+_db.Ext().GetID(pilot));
    	        if (_updateSuccess){
    	        	Console.WriteLine(Name + "Updated pilot: " + pilot);
    	        }
                Console.WriteLine();
    	        /* The object version is not valid after commit
    	         * - should be removed
    	         */
    	        _idVersions.Remove(id);
    		}
	        
    	} finally {
    		_db.Close();
    	}
       }
        // end Run
    }
}