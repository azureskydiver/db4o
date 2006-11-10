/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Semaphores
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
    
		public static IObjectContainer Login()
		{
        
			IObjectContainer objectContainer;
			try 
			{
				objectContainer = Db4oFactory.OpenClient(HOST, PORT, USER, PASSWORD);
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
