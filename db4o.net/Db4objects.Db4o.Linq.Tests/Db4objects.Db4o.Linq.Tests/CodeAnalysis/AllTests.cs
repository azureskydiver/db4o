﻿/* Copyright (C) 2007 - 2008  Versant Inc.  http://www.db4o.com */

using System;

using Db4oUnit.Extensions;

namespace Db4objects.Db4o.Linq.Tests.CodeAnalysis
{
	public class AllTests : Db4oTestSuite
	{
		protected override Type[] TestCases()
		{
			return new Type[] {
				typeof(ActivatedPropertyQueryTestCase),
				typeof(OrderByPropertyTestCase),
				typeof(PropertyQueryTestCase),
			};
		}
	}
}
