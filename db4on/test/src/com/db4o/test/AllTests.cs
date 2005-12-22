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

			//            Configuration conf = Db4o.configure();
			//            conf.messageLevel(-1);
			//
			//            //        conf.automaticShutDown(false);
			//            //        conf.lockDatabaseFile(false);
			//            //        conf.singleThreadedClient(true);
			//            //        conf.automaticShutDown(false);
			//            //        conf.lockDatabaseFile(false);
			//            //        conf.weakReferences(false);
			//            //        conf.callbacks(false);
			//            //        conf.detectSchemaChanges(false);
			//            //        conf.testConstructors(false);
			//            //        conf.discardFreeSpace(Integer.MAX_VALUE);
			//            //        conf.encrypt(true);
			//
			//            // BenchMark.Main(null);
			//
			new AllTests().run();
		}

		public AllTests() : base() 
		{
			Tester.currentRunner = this;
			if(Compat.compact())
			{
				CLIENT_SERVER = false;
			}
		}

		public void run1()
		{
			Thread.sleep(4000);
		}
      
		public virtual void run() 
		{
			Db4o.configure().messageLevel(-1);
			logConfiguration();
			long time1 = JavaSystem.currentTimeMillis();
			if(DELETE_FILE)
			{
				Tester.delete();
			}
			configure();
            
			for (Tester.run = 1; Tester.run <= RUNS; Tester.run++) 
			{
				Console.WriteLine("com.db4o.AllTests run " + Tester.run + " from " + RUNS);
				if (SOLO) 
				{
					Tester.runServer = false;
					Tester.clientServer = false;
					runTests();
				}
				if (CLIENT_SERVER) 
				{
					Tester.runServer = !REMOTE_SERVER;
					Tester.clientServer = true;
					runTests();
				}
				Tester.end();
			}
			time1 = JavaSystem.currentTimeMillis() - time1;
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
      
		protected void configure() 
		{
			for (int i1 = 0; i1 < TESTS.Length; i1++) 
			{
				Object toTest1 = newInstance(TESTS[i1]);
				runMethod(toTest1, "configure");
			}
		}
      
		private void runTests() 
		{
			String cs = Tester.clientServer ? "C/S" : "SOLO";
            foreach (Type test in TESTS)
			{
				Console.WriteLine(cs + " testing " +  test.Name);
				Object toTest1 = newInstance(test);
				Tester.open();
				if(! runStoreOne(toTest1))
				{
					runMethod(toTest1, "store");
				}
				Tester.commit();
				Tester.close();
				Tester.open();
				toTest1 = newInstance(test);
				runTestOne(toTest1);
				toTest1 = newInstance(test);
				Method[] methods1 = Class.getClassForType(test).getDeclaredMethods();
				for (int j1 = 0; j1 < methods1.Length; j1++) 
				{
					Method method1 = methods1[j1];
					String methodName = method1.getName();
					if (!equalsIgnoringCase(methodName, "testOne")) 
					{
						Tester.runIfTestMethod(method1, toTest1);
					}
				}
				Tester.close();
			}
		}

		private Object newInstance(Type type) 
		{
			Class clazz = Class.getClassForType(type);
			try 
			{
				return clazz.newInstance();
			}  
			catch (Exception e) 
			{
				Tester.error(e);
			}
			Console.WriteLine("Instantiation failed. Class:" + clazz.getName());
			return null;
		}
      
		private void runMethod(Object onObject, String methodName) 
		{
			try 
			{
				Method method1 = getMethod(onObject, methodName);
				if (method1 != null) 
				{
					method1.invoke(onObject, null);
				} 
			}  
			catch (Exception e) 
			{
				Tester.error(e);
			}
		}

		private bool runStoreOne(Object onObject) 
		{
			try 
			{
				Method method = getMethod(onObject, "storeOne");
				if (method != null) 
				{
					Tester.deleteAllInstances(onObject);
					method.invoke(onObject, null);
					Tester.store(onObject);
					return true;
				}
			} 
			catch (Exception e) 
			{
				Tester.error(e);
			}
			return false;
		}

		private bool runTestOne(Object onObject) 
		{
			try 
			{
				Method method = getMethod(onObject, "testOne");
				if (method != null) 
				{
					onObject = Tester.getOne(onObject);
					method.invoke(onObject, null);
					return true;
				}
			} 
			catch (Exception e) 
			{
				Tester.error(e);
			}
			return false;
		}

		private Method getMethod(Object onObject, string methodName)
		{
			Class clazz = Class.getClassForObject(onObject);
			foreach (Method m in clazz.getDeclaredMethods()) 
			{
				if (equalsIgnoringCase(methodName, m.getName()))
				{
					return m;
				}
			}
			return null;
		}

		private bool equalsIgnoringCase(string a, string b)
		{
			return 0 == string.Compare(a, b, true);
		}
      
		protected void logConfiguration() 
		{
			Console.WriteLine("\nRunning " + typeof(AllTests).FullName + " against\n" + Db4o.version() + "\n");
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
