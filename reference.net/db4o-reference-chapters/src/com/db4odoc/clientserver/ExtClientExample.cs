/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.Clientserver
{
	public class ExtClientExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public readonly static string ExtFileName ="formula1e.yap";
	
		public static void Main(string[] args) 
		{
			SwitchExtClients();
		}
		// end Main

		public static void SwitchExtClients() 
		{
			File.Delete(YapFileName);
			File.Delete(ExtFileName);
			IObjectServer server=Db4oFactory.OpenServer(YapFileName,0);
			try 
			{
				IObjectContainer client=server.OpenClient();
				Car car = new Car("BMW");
				client.Set(car);
				System.Console.WriteLine("Objects in the Main database file:");
				RetrieveAll(client);
			
				System.Console.WriteLine("Switching to additional database:");
				IExtClient clientExt = (IExtClient)client;
				clientExt.SwitchToFile(ExtFileName);
				car = new Car("Ferrari");
				clientExt.Set(car);
				RetrieveAll(clientExt);
				System.Console.WriteLine("Main database file again: ");
				clientExt.SwitchToMainFile();
				RetrieveAll(clientExt);
				clientExt.Close();
			}
			finally 
			{
				server.Close();
			}
		}
		// end SwitchExtClients

		public static void RetrieveAll(IObjectContainer db) 
		{
			IObjectSet result = db.Query(typeof(Car));
			ListResult(result);
		}
		// end RetrieveAll

		public static void ListResult(IObjectSet result)
		{
			Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				Console.WriteLine(item);
			}
		}
		// end ListResult
	}
}
