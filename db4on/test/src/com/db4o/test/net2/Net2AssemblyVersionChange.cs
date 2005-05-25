namespace com.db4o.test.net2
{
#if NET_2_0
	using System;
	using System.Collections.Generic;
	using System.IO;
	using System.Reflection;
	using System.Text;
	using System.CodeDom;
	using System.CodeDom.Compiler;

    class Net2AssemblyVersionChange
    {
        static readonly string DataFile = Path.Combine(Path.GetTempPath(), "test.yap");

        public void test()
        {
            /**
             * This test emits an assembly with a version in one app domain
             * and then unloads it.
             * 
             * Then emits the same assembly with a different version in a second
             * AppDomain, and then tries to load the classes from it.
             */

            string baseCode = @"
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

public class Test
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

            string version1Code = "[assembly: System.Reflection.AssemblyVersion(\"1.0.0.0\")]";
            string version2Code = "[assembly: System.Reflection.AssemblyVersion(\"2.0.0.0\")]";

            string appDomain1BasePath = Path.Combine(Path.GetTempPath(), "appdomain1");
            string appDomain2BasePath = Path.Combine(Path.GetTempPath(), "appdomain2");

            if (!Test.ensure(EmitAssembly(appDomain1BasePath, baseCode, version1Code)))
            {
                return;
            }
            if (!Test.ensure(EmitAssembly(appDomain2BasePath, baseCode, version2Code)))
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
                Test.error(e);
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
                Type type = testAssembly.GetType("Test", true);
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

        public bool CompileFiles(string assemblyFName, string[] files)
        {
            CompilerInfo info = CodeDomProvider.GetCompilerInfo(CodeDomProvider.GetLanguageFromExtension(".cs"));
            using (CodeDomProvider provider = info.CreateProvider())
            {
                CompilerParameters parameters = info.CreateDefaultCompilerParameters();
                parameters.IncludeDebugInformation = true;
                parameters.OutputAssembly = assemblyFName;
                parameters.ReferencedAssemblies.Add(typeof(Db4o).Assembly.Location);
                CompilerResults results = provider.CompileAssemblyFromFile(parameters, files);
                if (results.Errors.Count > 0)
                {
                    Test.ensure(GetErrorString(results.Errors), false);
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
    }
#endif
}
