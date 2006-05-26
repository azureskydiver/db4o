using System;
using System.Diagnostics;
using System.IO;
using com.db4o.config;

namespace com.db4o.test.aliases
{
	public class Person1
	{
		private String _name;

		public Person1(String name)
		{
			_name = name;
		}

		public String Name
		{
			get { return _name; }
		}
	}

	public class Person2
	{
		private String _name;

		public Person2(String name)
		{
			_name = name;
		}

		public String Name
		{
			get { return _name; }
		}

		public override bool Equals(object obj)
		{
			Person2 other = obj as Person2;
			if (null == other) return false;
			return object.Equals(_name, other._name);
		}
	}

	/// <summary>
	/// </summary>
	public class ClassAliasesTestCase
	{
		public void TestTypeAlias()
		{
			ObjectContainer container = Tester.ObjectContainer();

			container.Set(new Person1("Homer Simpson"));
			container.Set(new Person1("John Cleese"));

			container = Tester.ReOpen();
			container.Ext().Configure().AddAlias(
				// Person1 instances should be read as Person2 objects
				new TypeAlias(
				"com.db4o.test.aliases.Person1, db4o-net-test",
				"com.db4o.test.aliases.Person2, db4o-net-test"));
			AssertData(container);
		}

		private void AssertData(ObjectContainer container)
		{
			ObjectSet os = container.Get(typeof(Person2));
			Tester.EnsureEquals(2, os.Size());
			EnsureContains(os, new Person2("Homer Simpson"));
			EnsureContains(os, new Person2("John Cleese"));
		}

		public void TestAccessingJavaFromDotnet()
		{
			if (null == db4obuild())
			{
				Console.WriteLine("'db4obuild' directory not found, skipping java compatibility test.");
				return;
			}

			GenerateJavaData();
			using (ObjectContainer container = OpenJavaDataFile())
			{
				container.Ext().Configure().AddAlias(
					new WildcardAlias(
					"com.db4o.test.aliases.*",
					"com.db4o.test.aliases.*, db4o-net-test"));
				AssertData(container);
			}
		}

		private ObjectContainer OpenJavaDataFile()
		{
			return Db4o.OpenFile(GetJavaDataFile());
		}

		private String GetJavaDataFile()
		{
			return BuildTempPath("java.yap");
		}

		private String BuildTempPath(String fname)
		{
			return Path.Combine(Path.GetTempPath(), fname);
		}

		private void GenerateJavaData()
		{
			File.Delete(GetJavaDataFile());
			GenerateClassFile();
			string stdout = Exec("java", "-cp ." + Path.PathSeparator + Db4ojarPath(), "com.db4o.test.aliases.Program", Quote(GetJavaDataFile()));
			Console.WriteLine(stdout);
		}

		private void GenerateClassFile()
		{
			String code = @"
package com.db4o.test.aliases;

import com.db4o.*;

class Person2 {
	String _name;
	public Person2(String name) {
		_name = name;
	}
}

public class Program {
	public static void main(String[] args) {
		String fname = args[0];
		ObjectContainer container = Db4o.openFile(fname);
		container.set(new Person2(""Homer Simpson""));
		container.set(new Person2(""John Cleese""));
		container.close();
		System.out.println(""success"");
	}
}";

			string srcFile = BuildTempPath("com/db4o/test/aliases/Program.java");
			WriteFile(srcFile, code);
			string stdout = Exec(JavacPath(), "-classpath " + Db4ojarPath(), Quote(srcFile));
			Console.WriteLine(stdout);
		}

		static string Quote(string s)
		{
			return "\"" + s + "\"";
		}

		private string JavacPath()
		{
			string path = ReadProperty(MachinePropertiesPath(), "file.compiler.jdk1.3");
			Tester.Ensure(File.Exists(path));
			return path;
		}

		private string ReadProperty(string fname, string property)
		{
			using (StreamReader reader = File.OpenText(fname))
			{
				string line = null;
				while (null != (line = reader.ReadLine()))
				{
					if (line.StartsWith(property))
					{
						return line.Substring(property.Length + 1);
					}
				}
			}
			throw new ArgumentException("property '" + property + "' not found in '" + fname + "'");
		}

		private string MachinePropertiesPath()
		{
			string path = WorkspacePath("db4obuild/machine.properties");
			Tester.Ensure(path, File.Exists(path));
			return path;
		}

		private String Exec(String program, params String[] arguments)
		{
			ProcessStartInfo psi = new ProcessStartInfo(program);
			psi.UseShellExecute = false;
			psi.Arguments = string.Join(" ", arguments);
			psi.RedirectStandardOutput = true;
			psi.RedirectStandardError = true;
			psi.WorkingDirectory = Path.GetTempPath();
			psi.CreateNoWindow = true;

			Process p = Process.Start(psi);
			string stdout = p.StandardOutput.ReadToEnd();
			string stderr = p.StandardError.ReadToEnd();
			p.WaitForExit();
			return stdout + stderr;
		}

		private String Db4ojarPath()
		{
			string db4oVersion = string.Format("{0}.{1}", Db4oVersion.MAJOR, Db4oVersion.MINOR);
			return WorkspacePath("db4obuild/dist/java/lib/db4o-" + db4oVersion + "-java1.2.jar");
		}

		private string WorkspacePath(string fname)
		{
			return Path.Combine(db4obuild(), fname);
		}

		private string db4obuild()
		{
			return FindParentDirectory("db4obuild");
		}

		private string FindParentDirectory(string path)
		{
			string parent = Path.GetFullPath("..");
			while (true)
			{
				if (Directory.Exists(Path.Combine(parent, path))) return parent;
				string oldParent = parent;
				parent = Path.GetDirectoryName(parent);
				if (parent == oldParent || parent == null) break;
			}
			return null;
		}

		private void WriteFile(String fname, String contents)
		{
			Directory.CreateDirectory(Path.GetDirectoryName(fname));
			using (StreamWriter writer = new StreamWriter(fname))
			{
				writer.Write(contents);
			}
		}

		private void EnsureContains(ObjectSet actual, Object expected)
		{
			actual.Reset();
			while (actual.HasNext())
			{
				Object next = actual.Next();
				if (object.Equals(next, expected)) return;
			}
			Tester.Ensure(false);
		}

	}
}
