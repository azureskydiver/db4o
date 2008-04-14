/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.IO;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.IOs
{
	public class IOExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args) 
		{
			SetObjects();
			GetObjectsInMem();
			GetObjects();
			TestLoggingAdapter();
		}
		// end Main
	
		private static void SetObjects()
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void GetObjectsInMem()
		{
			System.Console.WriteLine("Setting up in-memory database");
			MemoryIoAdapter adapter = new MemoryIoAdapter();
			try 
			{
				Sharpen.IO.RandomAccessFile raf = new Sharpen.IO.RandomAccessFile(Db4oFileName,"r"); 
				adapter.GrowBy(100);
			
				int len = (int)raf.Length();
				byte[] b = new byte[len];
				raf.Read(b,0,len);
				adapter.Put(Db4oFileName, b);
				raf.Close();
			} 
			catch (Exception ex)
			{
				System.Console.WriteLine("Exception: " + ex.Message);
			}
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Io(adapter);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
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
			byte[] dbstream = adapter.Get(Db4oFileName);
			try 
			{
                Sharpen.IO.RandomAccessFile file = new Sharpen.IO.RandomAccessFile(Db4oFileName, "rw");
				file.Write(dbstream);
				file.Close();
			} 
			catch (IOException ioex) 
			{
				System.Console.WriteLine("Exception: " + ioex.Message);
			}
		}
		// end GetObjectsInMem

        private static void GetObjects()
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Io(new RandomAccessFileAdapter());
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
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

        private static void TestLoggingAdapter()
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Io(new LoggingAdapter());
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
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

            db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try 
			{
				IObjectSet result=db.Get(typeof(Pilot));
				ListResult(result);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end TestLoggingAdapter

        private static void ListResult(IObjectSet result)
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