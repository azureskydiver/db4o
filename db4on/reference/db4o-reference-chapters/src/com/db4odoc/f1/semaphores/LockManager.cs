/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using com.db4o;
using com.db4o.ext;

namespace com.db4odoc.f1.semaphores
{
	/**
	 * This class demonstrates a very rudimentary implementation
	 * of virtual "locks" on objects with db4o. All code that is
	 * intended to obey these locks will have to call lock() and
	 * unlock().  
	 */
	public class LockManager 
	{
    
		readonly private string SEMAPHORE_NAME = "locked: ";
		readonly private int WAIT_FOR_AVAILABILITY = 300; // 300 milliseconds
    
		readonly private ExtObjectContainer _objectContainer;
    
		public LockManager(ObjectContainer objectContainer)
		{
			_objectContainer = objectContainer.Ext();
		}
    
		public bool Lock(object obj)
		{
			long id = _objectContainer.GetID(obj);
			return _objectContainer.SetSemaphore(SEMAPHORE_NAME + id, WAIT_FOR_AVAILABILITY);
		}
    
		public void Unlock(Object obj)
		{
			long id = _objectContainer.GetID(obj);
			_objectContainer.ReleaseSemaphore(SEMAPHORE_NAME + id);
		}
	}

}
