/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

using System;
using Db4oAdmin.Tests.Framework;

namespace Db4oAdmin.Tests
{
	class Program
	{	
		public const string DatabaseFile = "subject.yap";

		public static int Main(string[] args)
		{
			try
			{
				return new Program().Run();
			}
			catch (Exception x)
			{
				Console.WriteLine(x);
				return 255;
			}
		}

		private TestRunner _runner;
		
		public Program()
		{
			_runner = new TestRunner();
		}

		int Run()
		{
			_runner.RunTestCase(typeof(ILPatternTestCase));
			_runner.RunTestCase(typeof(CFNQRuntimeOptimizationTestCase));
			_runner.RunTestCase(typeof(PredicateBuildTimeOptimizationTestCase));
			_runner.Report();
			return _runner.Failures;
		}
	}
}
