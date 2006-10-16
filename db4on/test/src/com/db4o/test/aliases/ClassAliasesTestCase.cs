using System;
using System.Diagnostics;
using System.IO;
using com.db4o.config;
using com.db4o.db4ounit.util;

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
			return CFHelper.AreEqual(_name, other._name);
		}
	}
	
	class CFHelper
	{
		public static bool AreEqual(object l, object r)
		{
			if (l == r) return true;
			if (l == null || r == null) return false;
			return l.Equals(r);
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

#if !CF_1_0 && !CF_2_0
		public void TestAccessingJavaFromDotnet()
		{	
			if (Tester.IsClientServer()) return;
			
			if (null == WorkspaceServices.WorkspaceRoot)
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
			return IOServices.BuildTempPath("java.yap");
		}

		private void GenerateJavaData()
		{
			File.Delete(GetJavaDataFile());
			GenerateClassFile();
			string stdout = IOServices.Exec("java", "-cp ." + Path.PathSeparator + WorkspaceServices.Db4ojarPath(), "com.db4o.test.aliases.Program", Quote(GetJavaDataFile()));
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

			string srcFile = IOServices.BuildTempPath("com/db4o/test/aliases/Program.java");
			IOServices.WriteFile(srcFile, code);
			string stdout = IOServices.Exec(WorkspaceServices.JavacPath(), "-classpath " + WorkspaceServices.Db4ojarPath(), Quote(srcFile));
			Console.WriteLine(stdout);
		}

		static string Quote(string s)
		{
			return "\"" + s + "\"";
		}
#endif

		private void EnsureContains(ObjectSet actual, Object expected)
		{
			actual.Reset();
			while (actual.HasNext())
			{
				Object next = actual.Next();
				if (CFHelper.AreEqual(next, expected)) return;
			}
			Tester.Ensure(false);
		}

	}
}
