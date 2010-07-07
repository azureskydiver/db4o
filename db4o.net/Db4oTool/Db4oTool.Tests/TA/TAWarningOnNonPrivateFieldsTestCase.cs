﻿/* Copyright (C) 2009 Versant Corporation.   http://www.db4o.com */
using Db4oTool.Tests.Core;
using Mono.Cecil;

namespace Db4oTool.Tests.TA
{
	class TAWarningOnNonPrivateFieldsTestCase : TAOutputListenerTestCaseBase
	{
		public void TestWarningOnNonPrivateFields()
		{
			AssemblyDefinition assembly = GenerateAssembly("TAClassWithNonPrivateFieldsSubject");
			InstrumentAndAssert(
				AssemblyPath(assembly), 
				true,
				ResultFor("TAClassWithPublicFieldSubject", "value"),
				ResultFor("TAClassWithProtectedFieldSubject", "value"));
		}

		public void TestNoWarningsForNonInstrumentedClasses()
		{
			CompilationServices.ExtraParameters.Using(
				"/d:" + TargetPlatformDefinition(),
				delegate
					{
						AssemblyDefinition assembly = GenerateAssembly("TANoFalsePositiveWarningsForNonPrivateFields");
						InstrumentAndAssert(
								AssemblyPath(assembly),
								"-v -ta -by-name:TAFilteredOut -not",
								true,
								ResultFor("TAMixOfPersistentAndNoPersistentFields", "_persistentInt"));
					});
		}

		private static string AssemblyPath(AssemblyDefinition assembly)
		{
			return assembly.MainModule.FullyQualifiedName;
		}

		private static string ResultFor(string typeName, string fieldName)
		{
			return string.Format("Found non-private field '{0}' in instrumented type '{1}'", fieldName, typeName);
		}

		private static string TargetPlatformDefinition()
		{
#if NET_2_0
			return "NET_2_0";
#else
			return "NET_3_5";
#endif
		}
	}
}
