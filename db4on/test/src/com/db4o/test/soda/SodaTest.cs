/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Diagnostics;
using com.db4o.foundation;
using j4o.lang;
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
			println("Starting S.O.D.A. test");
			testCases = 0;
			time = j4o.lang.JavaSystem.currentTimeMillis();
			SodaTest st1 = new SodaTest();
			st1.run(CLASSES, ENGINES, quiet);
			st1.completed();
		}
      
		protected void completed() 
		{
			time = j4o.lang.JavaSystem.currentTimeMillis() - time;
			println(name() + " completed. " + time + " ms");
			println("Tester cases: " + testCases);
			println("");
			println("");
			println("");
		}

		public static int failedClassesSize()
		{
			return failedTestClasses.size();
		}
      
		protected virtual String name() 
		{
			return "S.O.D.A. functionality test";
		}
      
		public void run(STClass[] classes, STEngine[] engines, bool quiet)
		{
			failedTestClasses = new Collection4();
			setSodaTestOn(classes);
			for (int i1 = 0; i1 < engines.Length; i1++) 
			{
				engine = engines[i1];
				engine.reset();
				engine.open();
				store(classes);
				engine.commit();
				engine.close();
				engine.open();
				test(classes);
				engine.close();
				engine.reset();
			}
			if (failedTestClasses.size() > 0) 
			{
				j4o.lang.JavaSystem.err.println("\nFailed test classes:\n");
				Iterator4 i1 = failedTestClasses.fastIterator();
				while (i1.hasNext()) 
				{
					j4o.lang.JavaSystem.err.println(j4o.lang.Class.getClassForObject(i1.next()).getName());
				}
				j4o.lang.JavaSystem.err.println("\n");
			}
		}
      
		protected void store(STClass[] classes) 
		{
			for (int i1 = 0; i1 < classes.Length; i1++) 
			{
				if (jdkOK(classes[i1])) 
				{
					Object[] objects1 = classes[i1].store();
					if (objects1 != null) 
					{
						for (int j1 = 0; j1 < objects1.Length; j1++) 
						{
							engine.store(objects1[j1]);
						}
					}
				}
			}
		}
      
		/**
		 * dynamic execution of all public methods that begin with "test" in all CLASSES 
		 */
		protected void test(STClass[] classes) 
		{
			for (int i1 = 0; i1 < classes.Length; i1++) 
			{
				if (jdkOK(classes[i1])) 
				{
					println("  S.O.D.A. testing " + classes[i1].GetType().FullName);
					currentTestClass = classes[i1];
					Method[] methods1 = j4o.lang.Class.getClassForObject(classes[i1]).getDeclaredMethods();
					for (int j1 = 0; j1 < methods1.Length; j1++) 
					{
						Tester.runIfTestMethod(methods1[j1], currentTestClass);
					}
				}
			}
		}
      
		protected static bool jdkOK(Object obj) 
		{
			return true;
		}
      
		public Query query() 
		{
			return engine.query();
		}
      
		public void expectOne(Query query, Object obj) 
		{
			expect(query, new Object[]{
										  obj         });
		}
      
		public void expectNone(Query query) 
		{
			expect(query, null);
		}
      
		public void expect(Query query, Object[] results) 
		{
			expect(query, results, false);
		}
      
		public void expectOrdered(Query query, Object[] results) 
		{
			expect(query, results, true);
		}
      
		private void expect(Query query, Object[] results, bool ordered) 
		{
			testCases++;
			ObjectSet set1 = query.execute();
			if (results == null || results.Length == 0) 
			{
				if (set1.size() > 0) 
				{
					error("No content expected.");
				}
				return;
			}
			int j1 = 0;
			if (set1.size() == results.Length) 
			{
				while (set1.hasNext()) 
				{
					Object obj1 = set1.next();
					bool found1 = false;
					if (ordered) 
					{
						if (comparer.isEqual(results[j1], obj1)) 
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
								if (comparer.isEqual(results[i1], obj1)) 
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
						error("Object not expected: " + obj1);
					}
				}
				for (int i1 = 0; i1 < results.Length; i1++) 
				{
					if (results[i1] != null) 
					{
						error("Expected object not returned: " + results[i1]);
					}
				}
			} 
			else 
			{
				error("Unexpected size returned.\nExpected: " + results.Length + " Returned: " + set1.size());
			}
		}
      
		public void error(String msg) 
		{
			if (!failedTestClasses.contains(currentTestClass)) 
			{
				failedTestClasses.add(currentTestClass);
			}
			if (!QUIET) 
			{
				println(msg + Compat.stackTrace());
			}
		}
      
		public static void log(Query query) 
		{
			ObjectSet set1 = query.execute();
			while (set1.hasNext()) 
			{
				STLogger.log(set1.next());
			}
		}

		public static void println(String str)
		{
			Console.WriteLine(str);
		}

		public static int testCaseCount()
		{
			return testCases;
		}
      
		protected void setSodaTestOn(STClass[] classes) 
		{
			for (int i1 = 0; i1 < classes.Length; i1++) 
			{
				try 
				{ 
					Field field1 = j4o.lang.Class.getClassForObject(classes[i1]).getDeclaredField("st");
					try 
					{ 
						Platform4.setAccessible(field1);
					}  
					catch (Exception t) 
					{ 
					}
					field1.set(classes[i1], this);
                      
				}  
				catch (Exception e) 
				{ 
					j4o.lang.JavaSystem.err.println("Add the following line to Class " + j4o.lang.Class.getClassForObject(classes[i1]).getName());
					j4o.lang.JavaSystem.err.println("public static transient SodaTest st;");
				}
			}
		}
	}
}