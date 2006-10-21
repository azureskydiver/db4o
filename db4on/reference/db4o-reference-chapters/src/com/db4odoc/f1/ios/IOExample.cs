/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using System.IO;
using com.db4o.io;
using com.db4o;
using com.db4o.query;

namespace com.db4odoc.f1.ios 
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
			ObjectContainer db = Db4o.OpenFile(YapFileName);
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
				j4o.io.RandomAccessFile raf = new j4o.io.RandomAccessFile(YapFileName,"r"); 
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
		
			Db4o.Configure().Io(adapter);
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result=db.Get(typeof(Pilot));
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
				j4o.io.RandomAccessFile file = new j4o.io.RandomAccessFile(YapFileName,"rw");
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
			Db4o.Configure().Io(new RandomAccessFileAdapter());
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result=db.Get(typeof(Pilot));
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
			Db4o.Configure().Io(new LoggingAdapter());
			ObjectContainer db = Db4o.OpenFile(YapFileName);
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
	
			db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result=db.Get(typeof(Pilot));
				ListResult(result);
			} 
			finally 
			{
				db.Close();
			}
			Db4o.Configure().Io(new RandomAccessFileAdapter());
		}
		// end TestLoggingAdapter
	
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