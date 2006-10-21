/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;
using com.db4o;
using com.db4o.config;
using com.db4o.ext;
using com.db4o.query;


namespace com.db4odoc.f1.semaphores
{

	/**
	 * This class demonstrates how semaphores can be used 
	 * to rule out race conditions when providing exact and
	 * up-to-date information about all connected clients 
	 * on a server. The class also can be used to make sure
	 * that only one login is possible with a give user name
	 * and ipAddress combination.
	 */
	public class ConnectedUser 
	{
    
		public readonly static string SEMAPHORE_CONNECTED = "ConnectedUser_";
		public readonly static string SEMAPHORE_LOCK_ACCESS = "ConnectedUser_Lock_";
    
		public readonly static int TIMEOUT = 10000;  // concurrent access timeout 10 seconds
    
		String userName;
		String ipAddress;
    
		public ConnectedUser(String userName, String ipAddress)
		{
			this.userName = userName;
			this.ipAddress = ipAddress;
		}
    
		// make sure to call this on the server before opening the database
		// to improve querying speed 
		public static void Configure()
		{
			ObjectClass objectClass = Db4o.Configure().ObjectClass(typeof(ConnectedUser)); 
			objectClass.ObjectField("userName").Indexed(true);
			objectClass.ObjectField("ipAddress").Indexed(true);
		}
    
		// call this on the client to ensure to have a ConnectedUser record 
		// in the database file and the semaphore set
		public static void Login(ObjectContainer client, String userName, String ipAddress)
		{
			if(! client.Ext().SetSemaphore(SEMAPHORE_LOCK_ACCESS, TIMEOUT))
			{
				throw new Exception("Timeout trying to get access to ConnectedUser lock");
			}
			Query q = client.Query();
			q.Constrain(typeof(ConnectedUser));
			q.Descend("userName").Constrain(userName);
			q.Descend("ipAddress").Constrain(ipAddress);
			if(q.Execute().Size() == 0)
			{
				client.Set(new ConnectedUser(userName, ipAddress));
				client.Commit();
			}
			String connectedSemaphoreName = SEMAPHORE_CONNECTED + userName + ipAddress;
			bool unique = client.Ext().SetSemaphore(connectedSemaphoreName, 0);
			client.Ext().ReleaseSemaphore(SEMAPHORE_LOCK_ACCESS);
			if(! unique)
			{
				throw new Exception("Two clients with same userName and ipAddress");
			}
		}
    
		// here is your list of all connected users, callable on the server
		public static IList ConnectedUsers(ObjectServer server)
		{
			ExtObjectContainer serverObjectContainer = server.Ext().ObjectContainer().Ext();
			if(serverObjectContainer.SetSemaphore(SEMAPHORE_LOCK_ACCESS, TIMEOUT))
			{
				throw new Exception("Timeout trying to get access to ConnectedUser lock");
			}
			IList list = new ArrayList();
			Query q = serverObjectContainer.Query();
			q.Constrain(typeof(ConnectedUser));
			ObjectSet objectSet = q.Execute();
			while(objectSet.HasNext())
			{
				ConnectedUser connectedUser = (ConnectedUser)objectSet.Next();
				String connectedSemaphoreName = 
					SEMAPHORE_CONNECTED + 
					connectedUser.userName + 
					connectedUser.ipAddress;
				if(serverObjectContainer.SetSemaphore(connectedSemaphoreName, TIMEOUT))
				{
					serverObjectContainer.Delete(connectedUser);
				}
				else
				{
					list.Add(connectedUser);
				}
			}
			serverObjectContainer.Commit();
			serverObjectContainer.ReleaseSemaphore(SEMAPHORE_LOCK_ACCESS);
			return list;
		}
	}
}
