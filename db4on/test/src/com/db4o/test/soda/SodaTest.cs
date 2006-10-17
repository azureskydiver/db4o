/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.foundation;
using j4o.lang.reflect;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda.arrays.obj;
using com.db4o.test.soda.arrays.typed;
using com.db4o.test.soda.arrays.untyped;
using com.db4o.test.soda.classes.simple;
using com.db4o.test.soda.classes.typedhierarchy;
using com.db4o.test.soda.classes.untypedhierarchy;
using com.db4o.test.soda.classes.wrapper.typed;
using com.db4o.test.soda.classes.wrapper.untyped;
using com.db4o.test.soda.collections;
using com.db4o.test.soda.engines.db4o;
using com.db4o.test.soda.experiments;
using com.db4o.test.soda.joins.typed;
using com.db4o.test.soda.joins.untyped;
using com.db4o.test.soda.ordered;
using com.db4o.test.soda.utils;
namespace com.db4o.test.soda 
{

	public class SodaTest 
	{
      
		public SodaTest() : base() 
		{
		}
		protected static bool QUIET = false;
		private static STEngine[] ENGINES = new STEngine[]{
															  new STDb4o()
															  // new STDb4oClientServer()      
														  };
		public static STClass[] CLASSES = new STClass[]{
														   new STArrayListT(),
														   new STArrayListU(),
														   new STArrMixed(),
														   new STArrStringO(),
														   new STArrStringON(),
														   new STArrStringT(),
														   new STArrStringTN(),
														   new STArrStringU(),
														   new STArrStringUN(),
														   new STArrIntegerO(),
														   new STArrIntegerON(),
														   new STArrIntegerT(),
														   new STArrIntegerTN(),
														   new STArrIntegerU(),
														   new STArrIntegerUN(),
														   new STArrIntegerWT(),
														   new STArrIntegerWTON(),
														   new STArrIntegerWUON(),
														   new STBoolean(),
														   new STBooleanWT(),
														   new STBooleanWU(),
														   new STByte(),
														   new STByteWT(),
														   new STByteWU(),
														   new STChar(),
														   new STCharWT(),
														   new STCharWU(),
														   new STDate(),
														   new STDateU(),
														   new STDecimal(),
														   new STDecimalU(),
														   new STDouble(),
														   new STDoubleWT(),
														   new STDoubleWU(),
														   new STETH1(),
														   new STFloat(),
														   new STFloatWT(),
														   new STFloatWU(),
														   new STHashtableD(),
														   new STHashtableED(),
														   new STHashtableET(),
														   new STHashtableEU(),
														   // new STHashtableT(),
														   new STHashtableU(),
														   new STIdentityEvaluation(),
														   new STInteger(),
														   new STIntegerWT(),
														   new STIntegerWU(),
														   new STLong(),
														   new STLongWT(),
														   new STLongWU(),
														   new STOrT(),
														   new STOrU(),
														   new STOString(),
														   new STOInteger(),
														   new STOIntegerWT(),
														   new STRTH1(),
														   new STSDFT1(),
														   new STShort(),
														   new STShortWT(),
														   new STShortWU(),
														   new STString(),
														   new STStringU(),
														   new STRUH1(),
														   new STTH1(),
														   new STUH1(),
														   new STMagic()
													   };
		protected static bool quiet = false;
		protected static STEngine engine;
		protected static int testCases;
		private STClass currentTestClass;
		protected static Collection4 failedTestClasses = new Collection4();
		private STCompare comparer = new STCompare();
		static internal long time;
      
		public static void Main(String[] args) 
		{
			Println("Starting S.O.D.A. test");
			testCases = 0;
			time = j4o.lang.JavaSystem.CurrentTimeMillis();
			SodaTest st1 = new SodaTest();
			st1.Run(CLASSES, ENGINES, quiet);
			st1.Completed();
		}
      
		protected void Completed() 
		{
			time = j4o.lang.JavaSystem.CurrentTimeMillis() - time;
			Println(Name() + " completed. " + time + " ms");
			Println("Tester cases: " + testCases);
			Println("");
			Println("");
			Println("");
		}

		public static int FailedClassesSize()
		{
			return failedTestClasses.Size();
		}
      
		protected virtual String Name() 
		{
			return "S.O.D.A. functionality test";
		}
      
		public void Run(STClass[] classes, STEngine[] engines, bool quiet)
		{
			failedTestClasses = new Collection4();
			SetSodaTestOn(classes);
			for (int i1 = 0; i1 < engines.Length; i1++) 
			{
				engine = engines[i1];
				engine.Reset();
				engine.Open();
				Store(classes);
				engine.Commit();
				engine.Close();
				engine.Open();
				Test(classes);
				engine.Close();
				engine.Reset();
			}
			if (failedTestClasses.Size() > 0) 
			{
				System.Console.Error.WriteLine("\nFailed test classes:\n");
				IEnumerator i1 = failedTestClasses.GetEnumerator();
				while (i1.MoveNext()) 
				{
					System.Console.Error.WriteLine(j4o.lang.Class.GetClassForObject(i1.Current).GetName());
				}
				System.Console.Error.WriteLine("\n");
			}
		}
      
		protected void Store(STClass[] classes) 
		{
			for (int i1 = 0; i1 < classes.Length; i1++) 
			{
				if (JdkOK(classes[i1])) 
				{
					Object[] objects1 = classes[i1].Store();
					if (objects1 != null) 
					{
						for (int j1 = 0; j1 < objects1.Length; j1++) 
						{
							engine.Store(objects1[j1]);
						}
					}
				}
			}
		}
      
		/**
		 * dynamic execution of all public methods that begin with "test" in all CLASSES 
		 */
		protected void Test(STClass[] classes) 
		{
			for (int i1 = 0; i1 < classes.Length; i1++) 
			{
				if (JdkOK(classes[i1])) 
				{
					Println("  S.O.D.A. testing " + classes[i1].GetType().FullName);
					currentTestClass = classes[i1];
					Method[] methods1 = j4o.lang.Class.GetClassForObject(classes[i1]).GetDeclaredMethods();
					for (int j1 = 0; j1 < methods1.Length; j1++) 
					{
						Tester.RunIfTestMethod(methods1[j1], currentTestClass);
					}
				}
			}
		}
      
		protected static bool JdkOK(Object obj) 
		{
			return true;
		}
      
		public Query Query() 
		{
			return engine.Query();
		}
      
		public void ExpectOne(Query query, Object obj) 
		{
			Expect(query, new Object[]{
										  obj         });
		}
      
		public void ExpectNone(Query query) 
		{
			Expect(query, null);
		}
      
		public void Expect(Query query, Object[] results) 
		{
			Expect(query, results, false);
		}
      
		public void ExpectOrdered(Query query, Object[] results) 
		{
			Expect(query, results, true);
		}
      
		private void Expect(Query query, Object[] results, bool ordered) 
		{
			testCases++;
			ObjectSet set1 = query.Execute();
			if (results == null || results.Length == 0) 
			{
				if (set1.Size() > 0) 
				{
					Error("No content expected.");
				}
				return;
			}
			int j1 = 0;
			if (set1.Size() == results.Length) 
			{
				while (set1.HasNext()) 
				{
					Object obj1 = set1.Next();
					bool found1 = false;
					if (ordered) 
					{
						if (comparer.IsEqual(results[j1], obj1)) 
						{
							results[j1] = null;
							found1 = true;
						}
						j1++;
					} 
					else 
					{
						for (int i1 = 0; i1 < results.Length; i1++) 
						{
							if (results[i1] != null) 
							{
								if (comparer.IsEqual(results[i1], obj1)) 
								{
									results[i1] = null;
									found1 = true;
									break;
								}
							}
						}
					}
					if (!found1) 
					{
						Error("Object not expected: " + obj1);
					}
				}
				for (int i1 = 0; i1 < results.Length; i1++) 
				{
					if (results[i1] != null) 
					{
						Error("Expected object not returned: " + results[i1]);
					}
				}
			} 
			else 
			{
				Error("Unexpected size returned.\nExpected: " + results.Length + " Returned: " + set1.Size());
			}
		}
      
		public void Error(String msg) 
		{
			if (!failedTestClasses.Contains(currentTestClass)) 
			{
				failedTestClasses.Add(currentTestClass);
			}
			if (!QUIET) 
			{
				Println(msg + Tester.StackTrace());
			}
		}
      
		public static void Log(Query query) 
		{
			ObjectSet set1 = query.Execute();
			while (set1.HasNext()) 
			{
				STLogger.Log(set1.Next());
			}
		}

		public static void Println(String str)
		{
			Console.WriteLine(str);
		}

		public static int TestCaseCount()
		{
			return testCases;
		}
      
		protected void SetSodaTestOn(STClass[] classes) 
		{
			for (int i1 = 0; i1 < classes.Length; i1++) 
			{
				try 
				{ 
					Field field1 = j4o.lang.Class.GetClassForObject(classes[i1]).GetDeclaredField("st");
					try 
					{ 
						Platform4.SetAccessible(field1);
					}  
					catch (Exception) 
					{ 
					}
					field1.Set(classes[i1], this);
                      
				}  
				catch (Exception) 
				{
					System.Console.Error.WriteLine("Add the following line to Class " + j4o.lang.Class.GetClassForObject(classes[i1]).GetName());
					System.Console.Error.WriteLine("public static transient SodaTest st;");
				}
			}
		}
	}
}