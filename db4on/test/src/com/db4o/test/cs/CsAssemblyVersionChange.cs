using System;
using System.IO;
using System.Reflection;

namespace com.db4o.test.cs
{
#if !CF_1_0 && !CF_2_0
	/// <summary>
	/// This test emits an assembly with a version in one app domain
	/// and then unloads it.
	/// 
	/// Then emits the same assembly with a different version in a second
	/// AppDomain, and then tries to load the classes from it.
	/// </summary>
	public class CsAssemblyVersionChange
	{
		const string TestAssemblyName = "test.exe";

		protected static readonly string DataFile = Path.Combine(Path.GetTempPath(), "test.yap");

		public void Test()
		{
			string version1Code = "[assembly: System.Reflection.AssemblyVersion(\"1.0.0.0\")]";
			string version2Code = "[assembly: System.Reflection.AssemblyVersion(\"2.0.0.0\")]";

			string appDomain1BasePath = Path.Combine(Path.GetTempPath(), "appdomain1");
			string appDomain2BasePath = Path.Combine(Path.GetTempPath(), "appdomain2");

			CompilationServices.EmitAssembly(Path.Combine(appDomain1BasePath, TestAssemblyName), BaseCode, version1Code);
			CompilationServices.EmitAssembly(Path.Combine(appDomain2BasePath, TestAssemblyName), BaseCode, version2Code);

			if (File.Exists(DataFile))
			{
				File.Delete(DataFile);
			}

			try
			{
				ExecuteTestMethodInAppDomain(appDomain1BasePath, "Store");
				ExecuteTestMethodInAppDomain(appDomain2BasePath, "Load");
			}
			catch (Exception e)
			{
				while (e is TargetInvocationException)
				{
					e = e.InnerException;
				}
				Tester.Error(e);
			}
		}

		[Serializable]
		class TestMethodRunner
		{
			string _methodName;

			public TestMethodRunner(string methodName)
			{
				_methodName = methodName;
			}

			public void Execute()
			{
				Assembly testAssembly = Assembly.LoadFrom(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, TestAssemblyName));
				Type type = testAssembly.GetType("Tester", true);
				MethodInfo method = type.GetMethod(_methodName);
				method.Invoke(null, new object[1] { DataFile });
			}
		}

		void ExecuteTestMethodInAppDomain(string basePath, string testMethod)
		{
			CopyNecessaryAssembliesTo(basePath);

			AppDomainSetup setup = new AppDomainSetup();
			setup.ApplicationBase = basePath;

			AppDomain domain = AppDomain.CreateDomain("db4o-assembly-test-domain", null, setup);
			try
			{
				domain.DoCallBack(new CrossAppDomainDelegate(new TestMethodRunner(testMethod).Execute));
			}
			finally
			{
				AppDomain.Unload(domain);
			}
		}

		void CopyNecessaryAssembliesTo(string basePath)
		{
			CopyToDir(typeof(Db4o).Assembly.Location, basePath);
			CopyToDir(Assembly.GetExecutingAssembly().Location, basePath);
		}

		void CopyToDir(string fname, string dir)
		{
			File.Copy(fname, Path.Combine(dir, Path.GetFileName(fname)), true);
		}

		string BaseCode
		{
			get
			{
#if NET_2_0
				#region .NET 2.0 version
				return @"
using System;
using System.IO;
using com.db4o;

public class SimpleGenericType<T>
{
	public T value;

	public SimpleGenericType(T value)
	{
		this.value = value;
	}
}

public class Tester
{
	public static void Store(string fname)
	{
		ObjectContainer container = Db4o.OpenFile(fname);
		try
		{
			container.Set(new SimpleGenericType<int>(42));
			container.Set(new SimpleGenericType<SimpleGenericType<int>>(new SimpleGenericType<int>(13)));
		}
		finally
		{
			container.Close();
		}
	}
	
	public static void Load(string fname)
	{
		ObjectContainer container = Db4o.OpenFile(fname);
		try
		{
			ObjectSet os = container.Get(typeof(SimpleGenericType<int>));
			AssertEquals(2, os.Size());
			
			os = container.Get(typeof(SimpleGenericType<SimpleGenericType<int>>));
			AssertEquals(1, os.Size());
		}
		finally
		{
			container.Close();
		}
	}
	
	static void AssertEquals(object expected, object actual)
	{
		if (!Object.Equals(expected, actual))
		{
			throw new ApplicationException();
		}
	}
}
            ";
				#endregion
#else
				#region .NET 1.1 version
				return @"
using System;
using System.IO;
using com.db4o;

public class ST
{
	public int value;

	public ST(int value)
	{
		this.value = value;
	}
}

public class Tester
{
	public static void Store(string fname)
	{
		ObjectContainer container = Db4o.OpenFile(fname);
		try
		{
			container.Set(new ST(42));
		}
		finally
		{
			container.Close();
		}
	}
	
	public static void Load(string fname)
	{
		ObjectContainer container = Db4o.OpenFile(fname);
		try
		{
			ObjectSet os = container.Get(typeof(ST));
			AssertEquals(1, os.Size());
		}
		finally
		{
			container.Close();
		}
	}
	
	static void AssertEquals(object expected, object actual)
	{
		if (!Object.Equals(expected, actual))
		{
			throw new ApplicationException();
		}
	}
}
            ";
				#endregion
#endif

			}
		}
	}
#endif
}
