/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
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
    
		readonly static string Host = "localhost";
		readonly static int Port = 4455;
		readonly static string User = "db4o";
		readonly static string Password= "db4o";
    
		readonly static int MaximumUsers = 10; 
    
		public static IObjectContainer Login()
		{
        
			IObjectContainer objectContainer;
			try 
			{
				objectContainer = Db4oFactory.OpenClient(Host, Port, User, Password);
			} 
			catch (IOException e) 
			{
				return null;
			}
        
			bool allowedToLogin = false;
        
			for (int i = 0; i < MaximumUsers; i++) 
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
