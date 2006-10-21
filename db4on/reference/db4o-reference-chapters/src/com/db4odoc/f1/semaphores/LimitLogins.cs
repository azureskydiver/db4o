/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using com.db4o;

namespace com.db4odoc.f1.semaphores
{
	/**
	 * This class demonstrates the use of semaphores to limit the
	 * number of logins to a server.
	 */
	public class LimitLogins 
	{
    
		readonly static string HOST = "localhost";
		readonly static int PORT = 4455;
		readonly static string USER = "db4o";
		readonly static string PASSWORD = "db4o";
    
		readonly static int MAXIMUM_USERS = 10; 
    
		public static ObjectContainer Login()
		{
        
			ObjectContainer objectContainer;
			try 
			{
				objectContainer = Db4o.OpenClient(HOST, PORT, USER, PASSWORD);
			} 
			catch (IOException e) 
			{
				return null;
			}
        
			bool allowedToLogin = false;
        
			for (int i = 0; i < MAXIMUM_USERS; i++) 
			{
				if(objectContainer.Ext().SetSemaphore("max_user_check_" + i, 0))
				{
					allowedToLogin = true;
					break;
				}
			}
        
			if(! allowedToLogin)
			{
				objectContainer.Close();
				return null;
			}
        
			return objectContainer;
		}
	}
}
