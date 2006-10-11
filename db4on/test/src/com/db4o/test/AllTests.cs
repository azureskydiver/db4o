/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.lang.reflect;

namespace com.db4o.test 
{

	/// <summary>
	///
	/// This is the main db4o regression test.
	///
	/// The parameters of the testing environment and all registered test
	/// cases can be found in AllTestsConfAll.cs.
	///
	/// Derive this class from AllTestsConfSingle if you only want to run
	/// single test cases and enter the test case that you want to run in
	/// the AllTestsConfSingle#TESTS[] array.
	/// </summary>
	///
	public class AllTests : AllTestsConfAll, Runnable  
	{

		public static void Main(String[] args) 
		{
			//            Configuration conf = Db4o.Configure();
			//            conf.MessageLevel(-1);
			//
			//            //        conf.AutomaticShutDown(false);
			//            //        conf.LockDatabaseFile(false);
			//            //        conf.SingleThreadedClient(true);
			//            //        conf.AutomaticShutDown(false);
			//            //        conf.LockDatabaseFile(false);
			//            //        conf.WeakReferences(false);
			//            //        conf.Callbacks(false);
			//            //        conf.DetectSchemaChanges(false);
			//            //        conf.TestConstructors(false);
			//            //        conf.DiscardFreeSpace(Integer.MAX_VALUE);
			//            //        conf.Encrypt(true);
			//
			//            // BenchMark.Main(null);
			//

			int errorCount=0;
			errorCount+=new com.db4o.db4ounit.common.AllTests().RunSolo();
#if NET_2_0 || CF_2_0
			errorCount += new com.db4o.db4ounit.cli2.AllTests().RunSolo();
#endif
			new AllTests().Run();
			errorCount+=Tester.errorCount;
			if(errorCount>0) 
			{
				System.Environment.Exit(errorCount);
			}
		}

		public AllTests() : base() 
		{
			Tester.currentRunner = this;
			if (Platform4.IsCompact())
			{
				EMBEDDED_CLIENT = true;
                SERVER_PORT = 0;
			}
		}

		public void Run1()
		{
			Thread.Sleep(4000);
		}
      
		public virtual void Run() 
		{
			Db4o.Configure().MessageLevel(-1);
			LogConfiguration();
			long time1 = JavaSystem.CurrentTimeMillis();
			if(DELETE_FILE)
			{
				Tester.Delete();
			}
			Configure();
            
			for (Tester.run = 1; Tester.run <= RUNS; Tester.run++) 
			{
				Console.WriteLine("com.db4o.AllTests run " + Tester.run + " from " + RUNS);
				if (SOLO) 
				{
					Tester.runServer = false;
					Tester.clientServer = false;
					RunTests();
				}
				if (CLIENT_SERVER) 
				{
					Tester.runServer = !REMOTE_SERVER;
					Tester.clientServer = true;
					RunTests();
				}
				Tester.End();
			}
			time1 = JavaSystem.CurrentTimeMillis() - time1;
			Console.WriteLine("\n\nAllTests completed.\nAssertions: " + Tester.assertionCount + "\nTime: " + time1 + "ms");
			if(Tester.errorCount == 0)
			{
				Console.WriteLine("No errors detected.\n");
			}
			else
			{
				Console.WriteLine("" + Tester.errorCount + " ERRORS DETECTED !!!.\n");
			}
		}
      
		protected void Configure() 
		{
			for (int i1 = 0; i1 < TESTS.Length; i1++) 
			{
				Object toTest1 = NewInstance(TESTS[i1]);
				RunMethod(toTest1, "configure");
			}
		}
      
		private void RunTests() 
		{
			String cs = Tester.clientServer ? "C/S" : "SOLO";
            foreach (Type test in TESTS)
			{
				Console.WriteLine(cs + " testing " +  test.Name);
				Object toTest1 = NewInstance(test);
				Tester.Open();
				if(! RunStoreOne(toTest1))
				{
					RunMethod(toTest1, "store");
				}
				Tester.Commit();
				Tester.Close();
				Tester.Open();
				toTest1 = NewInstance(test);
				RunTestOne(toTest1);
				toTest1 = NewInstance(test);
				Method[] methods1 = Class.GetClassForType(test).GetDeclaredMethods();
				for (int j1 = 0; j1 < methods1.Length; j1++) 
				{
					Method method1 = methods1[j1];
					String methodName = method1.GetName();
					if (!EqualsIgnoringCase(methodName, "testOne")) 
					{
						Tester.RunIfTestMethod(method1, toTest1);
					}
				}
				Tester.Close();
			}
		}

		private Object NewInstance(Type type) 
		{
			Class clazz = Class.GetClassForType(type);
			try 
			{
				return clazz.NewInstance();
			}  
			catch (Exception e) 
			{
				Tester.Error(e);
			}
			Console.WriteLine("Instantiation failed. Class:" + clazz.GetName());
			return null;
		}
      
		private void RunMethod(Object onObject, String methodName) 
		{
			try 
			{
				Method method1 = GetMethod(onObject, methodName);
				if (method1 != null) 
				{
					method1.Invoke(onObject, null);
				} 
			}  
			catch (Exception e) 
			{
				Tester.Error(e);
			}
		}

		private bool RunStoreOne(Object onObject) 
		{
			try 
			{
				Method method = GetMethod(onObject, "storeOne");
				if (method != null) 
				{
					Tester.DeleteAllInstances(onObject);
					method.Invoke(onObject, null);
					Tester.Store(onObject);
					return true;
				}
			} 
			catch (Exception e) 
			{
				Tester.Error(e);
			}
			return false;
		}

		private bool RunTestOne(Object onObject) 
		{
			try 
			{
				Method method = GetMethod(onObject, "testOne");
				if (method != null) 
				{
					onObject = Tester.GetOne(onObject);
					method.Invoke(onObject, null);
					return true;
				}
			} 
			catch (Exception e) 
			{
				Tester.Error(e);
			}
			return false;
		}

		private Method GetMethod(Object onObject, string methodName)
		{
			Class clazz = Class.GetClassForObject(onObject);
			foreach (Method m in clazz.GetDeclaredMethods()) 
			{
				if (EqualsIgnoringCase(methodName, m.GetName()))
				{
					return m;
				}
			}
			return null;
		}

		private bool EqualsIgnoringCase(string a, string b)
		{
			return 0 == string.Compare(a, b, true);
		}
      
		protected void LogConfiguration() 
		{
			Console.WriteLine("\nRunning " + typeof(AllTests).FullName + " against\n" + Db4o.Version() + "\n");
			Console.WriteLine("Using " + typeof(AllTests).BaseType.FullName + ".\n");
			Console.WriteLine("SERVER_HOSTNAME: " + SERVER_HOSTNAME);
			Console.WriteLine("SERVER_PORT: " + SERVER_PORT);
			Console.WriteLine("FILE_SERVER: " + FILE_SERVER);
			Console.WriteLine("FILE_SOLO: " + FILE_SOLO);
			Console.WriteLine("DELETE_FILE: " + DELETE_FILE);
			Console.WriteLine("BLOB_PATH: " + BLOB_PATH + "\n\n");
		}
	}
}
