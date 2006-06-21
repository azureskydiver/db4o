using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using com.db4o;
using com.db4o.inside.query;
using Db4oUnit;

namespace Db4oAdmin.Tests
{
	public abstract class AbstractInstrumentationTestCase : TestSuiteBuilder
	{
		public const string DatabaseFile = "subject.yap";
		
		class InstrumentationTestMethod : TestMethod
		{
			private AbstractInstrumentationTestCase _testCase;

			public InstrumentationTestMethod(AbstractInstrumentationTestCase testCase, object subject, MethodInfo method) : base(subject, method)
			{
				_testCase = testCase;
			}

			protected override void Invoke()
			{
				using (ObjectContainer container = _testCase.OpenDatabase())
				{
					GetMethod().Invoke(GetSubject(), new object[] { container });
				}
			}

			private Assembly CurrentDomain_AssemblyResolve(object sender, ResolveEventArgs args)
			{
				foreach (Assembly assembly in AppDomain.CurrentDomain.GetAssemblies())
				{
					if (assembly.GetName().Name == args.Name) return assembly;
				}
				return null;
			}

			override protected void SetUp()
			{
				base.SetUp();
				AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(CurrentDomain_AssemblyResolve);
			}

			override protected void TearDown()
			{
				AppDomain.CurrentDomain.AssemblyResolve -= new ResolveEventHandler(CurrentDomain_AssemblyResolve);
				base.TearDown();
			}
		}
		
		class InstrumentationTestSuiteBuilder : ReflectionTestSuiteBuilder
		{
			private AbstractInstrumentationTestCase _testCase;

			public InstrumentationTestSuiteBuilder(AbstractInstrumentationTestCase testCase, Type clazz)
				: base(clazz)
			{
				_testCase = testCase;
			}
			
			protected override bool IsTestMethod(MethodInfo method)
			{
				return method.Name.StartsWith("Test") && method.IsPublic;
			}

			protected override Test CreateTest(object instance, MethodInfo method)
			{
				return new InstrumentationTestMethod(_testCase, instance, method);
			}
		}

		public TestSuite Build()
		{
			string assemblyPath = EmitAssemblyFromResource();
			InstrumentAssembly(assemblyPath);
			
			Type type = GetTestCaseType(assemblyPath);
			TestSuite suite = new InstrumentationTestSuiteBuilder(this, type).Build();
			return new TestSuite(GetType().FullName, new Test[] { suite, new VerifyAssemblyTest(assemblyPath)});
		}
		
		protected abstract string ResourceName { get; }
		
		protected abstract void InstrumentAssembly(string path);
		
		protected abstract void OnQueryExecution(object sender, QueryExecutionEventArgs args);

		protected virtual void OnQueryOptimizationFailure(object sender, com.db4o.inside.query.QueryOptimizationFailureEventArgs args)
		{
			throw new ApplicationException(args.Reason.Message, args.Reason);
		}

		private static string GetResourceAsString(string resourceName)
		{
			using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream(resourceName))
			{
				return new StreamReader(stream).ReadToEnd();
			}
		}
		
		private Type GetTestCaseType(string assemblyName)
		{
			Assembly assembly = Assembly.LoadFrom(assemblyName);
			return assembly.GetType(ResourceName, true);
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
            CopyParentAssemblyToTemp(typeof(Assert));
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