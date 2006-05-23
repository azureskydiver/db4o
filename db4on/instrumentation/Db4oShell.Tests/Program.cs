/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using com.db4o;
using com.db4o.inside.query;

namespace Db4oShell.Tests
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
				return -1;
			}
		}

		private TestRunner _runner;
		
		public Program()
		{
			_runner = new TestRunner();
		}

		int Run()
		{
			RunILPatternTests();
			
			InstallDefaultAssemblyResolver();
			string assemblyPath = EmitAssemblyFromResource("Subject1.dll");
			InstrumentAssembly(assemblyPath);
			//RunTestsOutOfProcess();
			RunSubjectAssemblyTests(assemblyPath);
			
			return Report();
		}

		private void RunILPatternTests()
		{
			_runner.RunTestCase(typeof(ILPatternTestCase));
		}

		private int Report()
		{
			_runner.Report();
			return _runner.Failures;
		}

		private void InstrumentAssembly(string path)
		{
			new Db4oShell.Program(path).Run();
		}

		private string EmitAssemblyFromResource(string assemblyName)
		{
			CopyFileToFolder(typeof(ObjectContainer).Module.FullyQualifiedName, Path.GetTempPath());
			string assemblyPath = Path.Combine(Path.GetTempPath(), assemblyName);
			CompilationServices.EmitAssembly(assemblyPath, GetResourceAsString("Db4oShell.Tests.Resources.Subject.cs"));
			return assemblyPath;
		}

		private void CopyFileToFolder(string fname, string path)
		{
			File.Copy(fname, Path.Combine(path, Path.GetFileName(fname)), true);
		}

		private void InstallDefaultAssemblyResolver()
		{
			AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(CurrentDomain_AssemblyResolve);
		}

		Assembly CurrentDomain_AssemblyResolve(object sender, ResolveEventArgs args)
		{
			foreach (Assembly assembly in AppDomain.CurrentDomain.GetAssemblies())
			{
				if (assembly.GetName().Name == args.Name) return assembly;
			}
			return null;
		}

		private string GetResourceAsString(string resourceName)
		{
			using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream(resourceName))
			{
				return new StreamReader(stream).ReadToEnd();
			}
		}
		
		private void RunSubjectAssemblyTests(string assemblyName)
		{
			Assembly assembly = Assembly.LoadFrom(assemblyName);
			Type subject = assembly.GetType("Subject", true);
			RunTestMethodsWithContainer(_runner, subject);
			_runner.RunTest("AssemblyVerification", delegate { VerifyAssembly(assemblyName); });
		}

		private void RunTestMethodsWithContainer(TestRunner runner, Type subject)
		{
			foreach (MethodInfo method in EnumerateTestMethods(subject))
			{
				RunTestMethodWithContainer(runner, method);
			}
		}

		private static void RunTestMethodWithContainer(TestRunner runner, MethodInfo method)
		{
			using (ObjectContainer container = OpenDatabase())
			{
				Test test = delegate() { method.Invoke(null, new object[] { container }); };
				runner.RunTest(method.Name, test);
			}
		}

		private IEnumerable<MethodInfo> EnumerateTestMethods(Type subject)
		{
			foreach (MethodInfo method in subject.GetMethods())
			{
				if (method.Name.StartsWith("Test"))
				{
					yield return method;
				}
			}
		}

		private static void VerifyAssembly(string assemblyPath)
		{
			ProcessOutput output = shell("peverify.exe", assemblyPath);
			string stdout = output.StdOut;
			if (stdout.Contains("1.1.4322.573")) return; // ignore older peverify version errors
			if (output.ExitCode == 0 && !stdout.ToUpper().Contains("WARNING")) return;
			throw new ApplicationException(stdout);
		}

		private static void AssertIsMetaPredicateExecution(object sender, QueryExecutionEventArgs args)
		{
			Type type = typeof(MetaDelegate<object>).GetGenericTypeDefinition();
			if (args.Predicate.GetType().GetGenericTypeDefinition() != type)
			{
				throw new ApplicationException("Query invocation was not instrumented!");
			}
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
			p.StartInfo.Arguments = string.Join(" ", quote(args));
			p.Start();
			return p;
		}

		private static string[] quote(string[] args)
		{
			for (int i=0; i<args.Length; ++i)
			{
				args[i] = string.Format("\"{0}\"", args[i]);
			}
			return args;
		}

		private static ObjectContainer OpenDatabase()
		{
			if (File.Exists(DatabaseFile)) File.Delete(DatabaseFile);
			ObjectContainer container = Db4oFactory.OpenFile(DatabaseFile);
			NativeQueryHandler handler = ((YapStream)container).GetNativeQueryHandler();
			handler.QueryExecution += AssertIsMetaPredicateExecution;
			handler.QueryOptimizationFailure += OnQueryOptimizationFailure;
			return container;
		}

		static void OnQueryOptimizationFailure(object sender, com.db4o.inside.query.QueryOptimizationFailureEventArgs args)
		{
			throw new ApplicationException(args.Reason.Message, args.Reason);
		}
	}
}
