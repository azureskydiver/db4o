/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

using System;
using System.Diagnostics;
using com.db4o.inside.query;

namespace CFNativeQueriesEnabler.Tests
{
	class Program
	{
		const string TestSubject = "CFNativeQueriesEnabler.Tests.Subject.exe";

		static int Main(string[] args)
		{
			try
			{
				return new Program().Run();
			}
			catch (Exception x)
			{
				Console.WriteLine(x);
				return -1;
			}
		}

		int Run()
		{
			// Run the tests before any instrumentation.
			// The tests must be run out of process to avoid locking
			// the assembly file
			RunTestsOutOfProcess();
			InstrumentTestSubject();
			return RunTests();
		}

		private void InstrumentTestSubject()
		{
			new CFNativeQueriesEnabler.Program(TestSubject).Run();
		}

		private int RunTests()
		{
			CFNativeQueriesEnabler.Tests.Subject.Program.QueryExecution += AssertIsMetaPredicateExecution;
			CFNativeQueriesEnabler.Tests.Subject.Program.SetUp();

			TestRunner runner = new TestRunner();
			runner.RunTestCase(typeof(CFNativeQueriesEnabler.Tests.Subject.Program));
			runner.RunTest("AssemblyVerification", VerifyAssembly);
			runner.Report();
			return runner.Failures;
		}

		private static void VerifyAssembly()
		{
			ProcessOutput output = shell("peverify.exe", TestSubject);
			string stdout = output.StdOut;
			if (stdout.Contains("1.1.4322.573")) return; // ignore older peverify version errors
			if (output.ExitCode == 0 && !stdout.ToUpper().Contains("WARNING")) return;
			throw new ApplicationException(stdout);
		}

		private void AssertIsMetaPredicateExecution(object sender, QueryExecutionEventArgs args)
		{
			if (!(args.Predicate is MetaDelegate<System.Predicate<CFNativeQueriesEnabler.Tests.Subject.Item>>))
			{
				throw new ApplicationException("Query invocation was not instrumented!");
			}
		}

		private void RunTestsOutOfProcess()
		{
			shell(TestSubject);
		}
		
		class ProcessOutput
		{
			public int ExitCode;
			public string StdOut;
		}

		private static ProcessOutput shell(string fname, params string[] args)
		{
			Process p = StartProcess(fname, args);
			ProcessOutput output = new ProcessOutput();
			output.StdOut = p.StandardOutput.ReadToEnd();
			p.WaitForExit();
			output.ExitCode = p.ExitCode;
			return output;
		}

		public static Process StartProcess(string filename, params string[] args)
		{
			Process p = new Process();
			p.StartInfo.CreateNoWindow = true;
			p.StartInfo.UseShellExecute = false;
			p.StartInfo.RedirectStandardOutput = true;
			p.StartInfo.RedirectStandardInput = true;
			p.StartInfo.RedirectStandardError = true;
			p.StartInfo.FileName = filename;
			p.StartInfo.Arguments = string.Join(" ", args);
			p.Start();
			return p;
		}
	}
}
