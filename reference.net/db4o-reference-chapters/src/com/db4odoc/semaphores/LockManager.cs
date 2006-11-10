/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.Semaphores
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
    
		readonly private IExtObjectContainer _objectContainer;
    
		public LockManager(IObjectContainer objectContainer)
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
