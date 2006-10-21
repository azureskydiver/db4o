/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using com.db4o;
using com.db4o.ext;


namespace com.db4odoc.f1.clientserver
{
	public class ExtClientExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public readonly static string ExtFileName ="formula1e.yap";
	
		public static void Main(String[] args) 
		{
			SwitchExtClients();
		}
		// end Main

		public static void SwitchExtClients() 
		{
			File.Delete(YapFileName);
			File.Delete(ExtFileName);
			ObjectServer server=Db4o.OpenServer(YapFileName,0);
			try 
			{
				ObjectContainer client=server.OpenClient();
				Car car = new Car("BMW");
				client.Set(car);
				System.Console.WriteLine("Objects in the Main database file:");
				RetrieveAll(client);
			
				System.Console.WriteLine("Switching to additional database:");
				ExtClient clientExt = (ExtClient)client;
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

		public static void RetrieveAll(ObjectContainer db) 
		{
			ObjectSet result = db.Get(typeof(Object));
			ListResult(result);
		}
		// end RetrieveAll

		public static void ListResult(ObjectSet result)
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
