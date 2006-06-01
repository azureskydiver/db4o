using System;
using System.Collections.Generic;
using System.Text;
using com.db4o.inside.query;
using Db4oAdmin.Tests.Framework;

namespace Db4oAdmin.Tests
{
	public class PredicateBuildTimeOptimizationTestCase : AbstractInstrumentationTestCase
	{
		protected override string ResourceName
		{
			get { return "PredicateSubject"; }
		}

		protected override void InstrumentAssembly(string path)
		{
			new Db4oAdmin.NQOptimization(path).Run();
		}

		protected override void OnQueryExecution(object sender, QueryExecutionEventArgs args)
		{
			Assert.AreEqual(QueryExecutionKind.PreOptimized, args.ExecutionKind);
		}
	}
}
