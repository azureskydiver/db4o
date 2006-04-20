/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;

namespace CFNativeQueriesEnabler.Tests
{
	delegate void Test();

	class TestRunner
	{
		int _failures = 0;
		int _tests = 0;
		
		public int Failures
		{
			get { return _failures;  }
		}

		public void RunTestCase(Type type)
		{
			foreach (MethodInfo method in type.GetMethods(BindingFlags.Public | BindingFlags.Static))
			{
				if (!method.Name.StartsWith("Test")) continue;
				RunTestMethod(method);
			}
		}

		public void RunTestMethod(MethodInfo method)
		{
			RunTest(method.Name, delegate { method.Invoke(null, null); });
		}

		public void RunTest(string name, Test test)
		{
			++_tests;
			try
			{
				test();
			}
			catch (TargetInvocationException x)
			{
				Error(name, x.InnerException);
			}
			catch (Exception x)
			{
				Error(name, x);
			}
		}

		private void Error(string testName, Exception error)
		{
			Console.WriteLine("{0}) {1}: {2}", ++_failures, testName, error);
			Console.WriteLine();
		}

		public void Report()
		{
			Console.WriteLine("{0} out of {1} tests passed.", _tests - _failures, _tests);
		}
	}
}