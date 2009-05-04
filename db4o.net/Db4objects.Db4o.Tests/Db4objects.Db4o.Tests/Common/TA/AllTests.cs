/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using System;
using Db4oUnit.Extensions;

namespace Db4objects.Db4o.Tests.Common.TA
{
	public class AllTests : Db4oTestSuite
	{
		protected override Type[] TestCases()
		{
			return new Type[] { typeof(Db4objects.Db4o.Tests.Common.TA.Diagnostics.AllTests), 
				typeof(Db4objects.Db4o.Tests.Common.TA.Hierarchy.AllTests), typeof(Db4objects.Db4o.Tests.Common.TA.Nested.AllTests
				) };
		}
	}
}
