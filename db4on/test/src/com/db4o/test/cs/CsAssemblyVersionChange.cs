using System;
using System.IO;
using System.Reflection;
using System.Text;
using System.CodeDom;
using System.CodeDom.Compiler;

namespace com.db4o.test.cs
{
	/// <summary>
	/// This test emits an assembly with a version in one app domain
	/// and then unloads it.
	/// 
	/// Then emits the same assembly with a different version in a second
	/// AppDomain, and then tries to load the classes from it.
	/// </summary>
	public class CsAssemblyVersionChange
	{
		protected static readonly string DataFile = Path.Combine(Path.GetTempPath(), "test.yap");

		public virtual string BaseCode
		{
			get
            {
                return IsPascalCase
                    ? BaseCodePascalCase
                    : BaseCodeCamelCase;
            }
		}

		public void test()
		{
			string version1Code = "[assembly: System.Reflection.AssemblyVersion(\"1.0.0.0\")]";
			string version2Code = "[assembly: System.Reflection.AssemblyVersion(\"2.0.0.0\")]";

			string appDomain1BasePath = Path.Combine(Path.GetTempPath(), "appdomain1");
			string appDomain2BasePath = Path.Combine(Path.GetTempPath(), "appdomain2");

			if (!Tester.ensure(EmitAssembly(appDomain1BasePath, BaseCode, version1Code)))
			{
				return;
			}
			if (!Tester.ensure(EmitAssembly(appDomain2BasePath, BaseCode, version2Code)))
			{
				return;
			}
            
			if (File.Exists(DataFile))
			{
				File.Delete(DataFile);
			}

			try
			{
				ExecuteTestMethodInAppDomain(appDomain1BasePath, "store");
				ExecuteTestMethodInAppDomain(appDomain2BasePath, "load");
			}
			catch (Exception e)
			{
				while (e is TargetInvocationException)
				{
					e = ((TargetInvocationException)e).InnerException;
				}
				Tester.error(e);
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
				Assembly testAssembly = Assembly.LoadFrom(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "test.exe"));
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

		bool EmitAssembly(string basePath, params string[] code)
		{
			CreateDirectoryIfNeeded(basePath);

			string[] sourceFiles = WriteSourceFiles(basePath, code);
			return CompileFiles(Path.Combine(basePath, "test.exe"), sourceFiles);
		}

		void CreateDirectoryIfNeeded(string directory)
		{
			if (!Directory.Exists(directory))
			{
				Directory.CreateDirectory(directory);
			}
		}

		string[] WriteSourceFiles(string basePath, string[] code)
		{
			string[] sourceFiles = new string[code.Length];
			for (int i=0; i<code.Length; ++i)
			{
				string sourceFile = Path.Combine(basePath, "source" + i + ".cs");
				WriteFile(sourceFile, code[i]);
				sourceFiles[i] = sourceFile;
			}
			return sourceFiles;
		}

		void WriteFile(string fname, string contents)
		{
			using (StreamWriter writer = new StreamWriter(fname))
			{
				writer.Write(contents);
			}
		}

#if NET_2_0
		CompilerInfo GetCSharpCompilerInfo()
		{
			return CodeDomProvider.GetCompilerInfo(CodeDomProvider.GetLanguageFromExtension(".cs"));
		}
#endif

		protected CodeDomProvider GetCSharpCodeDomProvider()
		{
#if NET_2_0
			return GetCSharpCompilerInfo().CreateProvider();
#else
			return (CodeDomProvider)j4o.lang.Class.forName("Microsoft.CSharp.CSharpCodeProvider, System").newInstance();
#endif
		}

		protected CompilerParameters CreateDefaultCompilerParameters()
		{
#if NET_2_0
			return GetCSharpCompilerInfo().CreateDefaultCompilerParameters();
#else
			return new CompilerParameters();
#endif
		}

		public bool CompileFiles(string assemblyFName, string[] files)
		{	
			using (CodeDomProvider provider = GetCSharpCodeDomProvider())
			{
				CompilerParameters parameters = CreateDefaultCompilerParameters();
				parameters.IncludeDebugInformation = false;
				parameters.OutputAssembly = assemblyFName;
				parameters.ReferencedAssemblies.Add(typeof(Db4o).Assembly.Location);

				ICodeCompiler compiler = provider.CreateCompiler();
				CompilerResults results = compiler.CompileAssemblyFromFileBatch(parameters, files);
				if (results.Errors.Count > 0)
				{
					Tester.ensure(GetErrorString(results.Errors), false);
				}
				return 0 == results.Errors.Count;
			}
		}

		string GetErrorString(CompilerErrorCollection errors)
		{
			StringBuilder builder = new StringBuilder();
			foreach (CompilerError error in errors)
			{
				builder.Append(error.ToString());
				builder.Append(Environment.NewLine);
			}
			return builder.ToString();
		}

        bool IsPascalCase
        {
            get
            {
                return GetType().GetMethod("Test") != null;
            }
        }

        string BaseCodeCamelCase
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
	public static void store(string fname)
	{
		ObjectContainer container = Db4o.openFile(fname);
		try
		{
			container.set(new SimpleGenericType<string>(""spam""));
			container.set(new SimpleGenericType<SimpleGenericType<string>>(new SimpleGenericType<string>(""eggs"")));
		}
		finally
		{
			container.close();
		}
	}
	
	public static void load(string fname)
	{
		ObjectContainer container = Db4o.openFile(fname);
		try
		{
			ObjectSet os = container.get(typeof(SimpleGenericType<string>));
			assertEquals(2, os.size());
			
			os = container.get(typeof(SimpleGenericType<SimpleGenericType<string>>));
			assertEquals(1, os.size());
		}
		finally
		{
			container.close();
		}
	}
	
	static void assertEquals(object expected, object actual)
	{
		if (!Object.Equals(expected, actual))
		{
			throw new ApplicationException(string.Format(""'{0}' != '{1}'"", expected, actual));
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
	public static void store(string fname)
	{
		ObjectContainer container = Db4o.openFile(fname);
		try
		{
			container.set(new ST(42));
		}
		finally
		{
			container.close();
		}
	}
	
	public static void load(string fname)
	{
		ObjectContainer container = Db4o.openFile(fname);
		try
		{
			ObjectSet os = container.get(typeof(ST));
			assertEquals(1, os.size());
		}
		finally
		{
			container.close();
		}
	}
	
	static void assertEquals(object expected, object actual)
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
        
        string BaseCodePascalCase
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
	public static void store(string fname)
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
	
	public static void load(string fname)
	{
		ObjectContainer container = Db4o.OpenFile(fname);
		try
		{
			ObjectSet os = container.Get(typeof(SimpleGenericType<int>));
			assertEquals(2, os.Size());
			
			os = container.Get(typeof(SimpleGenericType<SimpleGenericType<int>>));
			assertEquals(1, os.Size());
		}
		finally
		{
			container.Close();
		}
	}
	
	static void assertEquals(object expected, object actual)
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
	public static void store(string fname)
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
	
	public static void load(string fname)
	{
		ObjectContainer container = Db4o.OpenFile(fname);
		try
		{
			ObjectSet os = container.Get(typeof(ST));
			assertEquals(1, os.Size());
		}
		finally
		{
			container.Close();
		}
	}
	
	static void assertEquals(object expected, object actual)
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
}
