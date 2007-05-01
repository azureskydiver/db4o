using System;
using Db4objects.Db4o.Tests.Util;
using Db4oUnit;

namespace Db4objects.Db4o.Tests.CLI1
{
	class MyTestClass
	{
		public MyTestClass()
		{ }

		public MyTestClass(string value1, string value2, string value3)
		{
			field1 = value1;
			field2 = value2;
			field3 = value3;
		}

		public string field1;
		public string field2;
		public string field3;
	}


	class UUIDJavaCompatibilityTestCase : ITestCase
	{
		public void _Test()
		{
			if (!JavaServices.CanRunJavaCompatibilityTests())
			{
				return;
			}

			GenerateDataFile();
			CompileJavaProgram();
			string output = RunJavaProgram();
			AssertJavaOutput(output);
		}

		private void AssertJavaOutput(string output)
		{
			Console.WriteLine(output);
			Assert.IsFalse(output.Contains("Exception"));
		}

		private string RunJavaProgram()
		{
			return JavaServices.java("com.db4o.test.uuidcompatibility.Program", JavaServices.Quote(DataFilePath()));
		}

		private void CompileJavaProgram()
		{
			String code = @"
package com.db4o.test.uuidcompatibility;

import com.db4o.*;
import com.db4o.ext.*;

public class Program {
	public static void main(String[] args) {
		ObjectContainer container = Db4o.openFile(args[0]);
		StoredClass[] storedClasses = container.ext().storedClasses();
		for (int i = 0; i < storedClasses.length; i++) {
			StoredClass storedClass = storedClasses[i];
			System.out.println(storedClass.getName());
			StoredField[] storedFields = storedClass.getStoredFields();
			for (int j = 0; j < storedFields.length; j++) {
				StoredField storedField = storedFields[j];
				System.out.println(""\t"" + storedField.getName());
			}
		}
		container.close();
		System.out.println(""success"");
	}
}";

			JavaServices.ResetJavaTempPath();
			string stdout = JavaServices.CompileJavaCode("com/db4o/test/uuidcompatibility/Program.java", code);
			Console.WriteLine(stdout);
		}


		private void GenerateDataFile()
		{
			Db4oFactory.Configure().GenerateUUIDs(int.MaxValue);
			using (IObjectContainer container = Db4oFactory.OpenFile(DataFilePath()))
			{
				MyTestClass test1 = new MyTestClass();
				container.Set(test1);

				MyTestClass test2 = new MyTestClass();
				container.Set(test2);

				container.Commit();
			}
		}

		private static string DataFilePath()
		{
			return IOServices.BuildTempPath("UUIDCompatibility.db4o");
		}
	}
}
