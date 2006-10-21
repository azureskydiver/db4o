/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using com.db4o;
using com.db4o.query;
using j4o.lang;

namespace com.db4odoc.f1.semaphores
{
	/**
	 * This class	demonstrates the use	of a	semaphore to ensure that only
	 * one instance of a	certain	class is stored to an ObjectContainer.
	 * 
	 * Caution	!!! The getSingleton method contains a commit()	call.	
	 */
	public class	Singleton 
	{
		/**
		 * returns	a singleton object of one	class for an	ObjectContainer.
		 * <br><b>Caution !!!	This	method contains a commit() call.</b> 
		 */
		public static	Object GetSingleton(ObjectContainer objectContainer, Class	clazz) 
		{
			Object obj =	queryForSingletonClass(objectContainer, clazz);
			if (obj != null) 
			{
				return obj;
			}

			String semaphore =	"Singleton#getSingleton_" + clazz.GetName();

			if (!objectContainer.Ext().SetSemaphore(semaphore,	10000)) 
			{
				throw new Exception("Blocked semaphore "	+ semaphore);
			}

			obj =	queryForSingletonClass(objectContainer, clazz);

			if (obj == null) 
			{

				try 
				{
					obj =	clazz.NewInstance();
				} 
				catch (Exception e) 
				{
					System.Console.WriteLine(e.Message);
				} 

				objectContainer.Set(obj);

				/* !!! CAUTION !!!
				 * There is a	commit	call here.
				 * 
				 * The commit call	is	necessary, so	other transactions
				 * can see the new inserted object.
				 */
				objectContainer.Commit();

			}

			objectContainer.Ext().ReleaseSemaphore(semaphore);

			return obj;
		}

		private	static Object queryForSingletonClass(ObjectContainer objectContainer, Class	clazz) 
		{
			Query q = objectContainer.Query();
			q.Constrain(clazz);
			ObjectSet	objectSet	= q.Execute();
			if (objectSet.Size() == 1)	
			{
				return objectSet.Next();
			}
			if (objectSet.Size() > 1) 
			{
				throw new Exception(
					"Singleton problem. Multiple	instances of: "	+ clazz.GetName());
			}
			return null;
		}

	}

}
