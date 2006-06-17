/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */
using System.Reflection;
using com.db4o.inside.query;

namespace Db4oAdmin.Tests
{
	using System;
	using System.IO;
	using System.Collections.Generic;
	using com.db4o;

	public class CFNQRuntimeOptimizationTestCase : AbstractInstrumentationTestCase
	{
		protected override string ResourceName
		{
			get { return "CFNQSubject"; }
		}
		
		override protected void InstrumentAssembly(string path)
		{
			new Db4oAdmin.CFNQEnabler(path).Run();
		}

		override protected void OnQueryExecution(object sender, QueryExecutionEventArgs args)
		{
			Type type = typeof(MetaDelegate<object>).GetGenericTypeDefinition();
			if (args.Predicate.GetType().GetGenericTypeDefinition() != type)
			{
				throw new ApplicationException("Query invocation was not instrumented!");
			}
		}
	}
}