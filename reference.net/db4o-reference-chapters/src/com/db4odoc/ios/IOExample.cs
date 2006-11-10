/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using System.IO;
using Db4objects.Db4o.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.IOs
{
	
	public class IOExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args) 
		{
			SetObjects();
			GetObjectsInMem();
			GetObjects();
			TestLoggingAdapter();
		}
		// end Main
	
		public static void SetObjects()
		{
			File.Delete(YapFileName);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Pilot pilot = new Pilot("Rubens Barrichello");
				db.Set(pilot);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetObjects
	
		public static void GetObjectsInMem()
		{
			System.Console.WriteLine("Setting up in-memory database");
			MemoryIoAdapter adapter = new MemoryIoAdapter();
			try 
			{
				Sharpen.IO.RandomAccessFile raf = new Sharpen.IO.RandomAccessFile(YapFileName,"r"); 
				adapter.GrowBy(100);
			
				int len = (int)raf.Length();
				byte[] b = new byte[len];
				raf.Read(b,0,len);
				adapter.Put(YapFileName, b);
				raf.Close();
			} 
			catch (Exception ex)
			{
				System.Console.WriteLine("Exception: " + ex.Message);
			}
		
			Db4oFactory.Configure().Io(adapter);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet result=db.Get(typeof(Pilot));
				System.Console.WriteLine("Read stored results through memory file");
				ListResult(result);
				Pilot pilotNew = new Pilot("Michael Schumacher");
				db.Set(pilotNew);
				System.Console.WriteLine("New pilot added");
			} 
			finally 
			{
				db.Close();
			}
			System.Console.WriteLine("Writing the database back to disc");
			byte[] dbstream = adapter.Get(YapFileName);
			try 
			{
                Sharpen.IO.RandomAccessFile file = new Sharpen.IO.RandomAccessFile(YapFileName, "rw");
				file.Write(dbstream);
				file.Close();
			} 
			catch (IOException ioex) 
			{
				System.Console.WriteLine("Exception: " + ioex.Message);
			}
		}
		// end GetObjectsInMem
	
		public static void GetObjects()
		{
			Db4oFactory.Configure().Io(new RandomAccessFileAdapter());
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet result=db.Get(typeof(Pilot));
				System.Console.WriteLine("Read stored results through disc file");
				ListResult(result);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end GetObjects

		public static void TestLoggingAdapter()
		{
			Db4oFactory.Configure().Io(new LoggingAdapter());
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Pilot pilot = new Pilot("Michael Schumacher");
				db.Set(pilot);
				System.Console.WriteLine("New pilot added");
			} 
			finally 
			{
				db.Close();
			}
	
			db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet result=db.Get(typeof(Pilot));
				ListResult(result);
			} 
			finally 
			{
				db.Close();
			}
			Db4oFactory.Configure().Io(new RandomAccessFileAdapter());
		}
		// end TestLoggingAdapter
	
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