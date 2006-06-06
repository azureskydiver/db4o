using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using com.db4o;
using com.db4o.inside.query;
using Db4oAdmin.Tests.Framework;

namespace Db4oAdmin.Tests
{
	public abstract class AbstractInstrumentationTestCase : TestCase
	{
		public const string DatabaseFile = "subject.yap";

		public void TestInstrumentation()
		{
			string _assemblyPath = EmitAssemblyFromResource();
			InstrumentAssembly(_assemblyPath);
			VerifyAssembly(_assemblyPath);
			RunSubjectAssemblyTests(_assemblyPath);
		}

		public override void SetUp()
		{
			AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(CurrentDomain_AssemblyResolve);
		}

		public override void TearDown()
		{
			AppDomain.CurrentDomain.AssemblyResolve -= new ResolveEventHandler(CurrentDomain_AssemblyResolve);	
		}

		protected abstract string ResourceName { get; }
		
		protected abstract void InstrumentAssembly(string path);
		
		protected abstract void OnQueryExecution(object sender, QueryExecutionEventArgs args);

		protected virtual void OnQueryOptimizationFailure(object sender, com.db4o.inside.query.QueryOptimizationFailureEventArgs args)
		{
			throw new ApplicationException(args.Reason.Message, args.Reason);
		}

		private Assembly CurrentDomain_AssemblyResolve(object sender, ResolveEventArgs args)
		{
			foreach (Assembly assembly in AppDomain.CurrentDomain.GetAssemblies())
			{
				if (assembly.GetName().Name == args.Name) return assembly;
			}
			return null;
		}

		private static string GetResourceAsString(string resourceName)
		{
			using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream(resourceName))
			{
				return new StreamReader(stream).ReadToEnd();
			}
		}

		protected void RunSubjectAssemblyTests(string assemblyName)
		{
			Assembly assembly = Assembly.LoadFrom(assemblyName);
			Type subject = assembly.GetType(ResourceName, true);
			RunTestMethodsWithContainer(subject);
		}

		private void RunTestMethodsWithContainer(Type subject)
		{
			foreach (MethodInfo method in EnumerateTestMethods(subject))
			{
				RunTestMethodWithContainer(method);
			}
		}

		private void RunTestMethodWithContainer(MethodInfo method)
		{
			using (ObjectContainer container = OpenDatabase())
			{
				method.Invoke(null, new object[] { container });
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

		protected static void VerifyAssembly(string assemblyPath)
		{
			ShellUtilities.ProcessOutput output = ShellUtilities.shell("peverify.exe", assemblyPath);
			string stdout = output.StdOut;
			if (stdout.Contains("1.1.4322.573")) return; // ignore older peverify version errors
			if (output.ExitCode == 0 && !stdout.ToUpper().Contains("WARNING")) return;
			throw new ApplicationException(stdout);
		}

		private ObjectContainer OpenDatabase()
		{
			if (File.Exists(DatabaseFile)) File.Delete(DatabaseFile);
			ObjectContainer container = Db4oFactory.OpenFile(DatabaseFile);
			NativeQueryHandler handler = ((YapStream)container).GetNativeQueryHandler();
			handler.QueryExecution += OnQueryExecution;
			handler.QueryOptimizationFailure += OnQueryOptimizationFailure;
			return container;
		}
		
		protected string EmitAssemblyFromResource()
		{
			string assemblyName = ResourceName + ".dll";
			CopyParentAssemblyToTemp(typeof(ObjectContainer));
			CopyParentAssemblyToTemp(GetType());
			string path = Path.Combine(Path.GetTempPath(), assemblyName);
			CompilationServices.EmitAssembly(path,
			                                 GetResourceAsString("Db4oAdmin.Tests.Resources." + ResourceName + ".cs"));
			return path;
		}

		private static void CopyParentAssemblyToTemp(Type type)
		{
			ShellUtilities.CopyFileToFolder(type.Module.FullyQualifiedName, Path.GetTempPath());
		}
	}
}