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
		public void testTypeAlias() 
		{
			ObjectContainer container = Tester.objectContainer();

			container.set(new Person1("Homer Simpson"));
			container.set(new Person1("John Cleese"));
		
			container = Tester.reOpen();
			container.ext().configure().alias(
				// Person1 instances should be read as Person2 objects
				new TypeAlias(
				"com.db4o.test.aliases.Person1, db4o-net-test",
				"com.db4o.test.aliases.Person2, db4o-net-test"));
			assertData(container);
		}

		private void assertData(ObjectContainer container) 
		{
			ObjectSet os = container.get(typeof(Person2));
			Tester.ensureEquals(2, os.size());
			ensureContains(os, new Person2("Homer Simpson"));
			ensureContains(os, new Person2("John Cleese"));
		}
	
		public void testAccessingJavaFromDotnet()
		{
			generateJavaData();
			using (ObjectContainer container = openJavaDataFile())
			{
				container.ext().configure().alias(
					new GlobAlias(
					"com.db4o.test.aliases.*",
					"com.db4o.test.aliases.*, db4o-net-test"));
				assertData(container);
			}
		}
		
		private ObjectContainer openJavaDataFile() 
		{
			return Db4o.openFile(getJavaDataFile());
		}
		
		private String getJavaDataFile() 
		{
			return buildTempPath("java.yap");
		}
		
		private String buildTempPath(String fname) 
		{
			return Path.Combine(Path.GetTempPath(), fname);
		}
		
		private void generateJavaData()
		{
			File.Delete(getJavaDataFile());
			generateClassFile();
			string stdout = exec("java", "-cp ." + Path.PathSeparator + db4ojarPath(), "com.db4o.test.aliases.Program", getJavaDataFile());
			Console.WriteLine(stdout);
		}
		
		private void generateClassFile()
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
		
			String srcFile = buildTempPath("com/db4o/test/aliases/Program.java");
			writeFile(srcFile, code);
			/// XXX: locate javac in a non platform specific way
			String stdout = exec("c:/java/jdk1.5.0_04/bin/javac.exe", "-cp " + db4ojarPath(), srcFile);
			//Console.WriteLine(stdout);
		}

		private String exec(String program, params String[] arguments)
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

		private String db4ojarPath()
		{
			string path = Path.Combine(FindParentDirectory("db4obuild"), "db4obuild/dist/java/lib/db4o-5.2-java1.2.jar");
			Tester.ensure(path, File.Exists(path));
			return path;
		}

		private string FindParentDirectory(string path)
		{
			string parent = Path.GetFullPath("..");
			while (true)
			{
				if (Directory.Exists(Path.Combine(parent, path))) return parent;
				string oldParent = parent;
				parent = Path.GetDirectoryName(parent);
				if (parent == oldParent) break;
			}
			throw new ArgumentException("Could not find path '" + path + "'");
		}

		private void writeFile(String fname, String contents)
		{
			Directory.CreateDirectory(Path.GetDirectoryName(fname));
			using (StreamWriter writer = new StreamWriter(fname))
			{
				writer.Write(contents);
			}
		}

		private void ensureContains(ObjectSet actual, Object expected) 
		{
			actual.reset();
			while (actual.hasNext()) 
			{
				Object next = actual.next();
				if (object.Equals(next, expected)) return;
			}
			Tester.ensure(false);
		}
	
	}
}
