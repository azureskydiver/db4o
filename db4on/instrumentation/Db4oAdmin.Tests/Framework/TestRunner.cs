/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;

namespace Db4oAdmin.Tests.Framework
{
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
			object test = Activator.CreateInstance(type);
			ITestSuiteBuilder builder = test as ITestSuiteBuilder;
			if (builder != null)
			{
				RunTestSuite(builder);
			}
			else
			{
				RunTest((ITest)test);
			}
		}

		private void RunTestSuite(ITestSuiteBuilder builder)
		{
			foreach (ITest test in builder.Build())
			{
				RunTest(test);
			}
		}

		public void RunTest(ITest test)
		{
			++_tests;
			try
			{
				test.Run();
			}
			catch (TargetInvocationException x)
			{
				Error(test.Name, x.InnerException);
			}
			catch (Exception x)
			{
				Error(test.Name, x);
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