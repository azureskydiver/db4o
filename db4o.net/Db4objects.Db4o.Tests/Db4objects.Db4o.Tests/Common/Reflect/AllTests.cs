/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using Db4oUnit.Extensions;
using Db4objects.Db4o.Tests.Common.Reflect;
using Db4objects.Db4o.Tests.Common.Reflect.Generic;

namespace Db4objects.Db4o.Tests.Common.Reflect
{
	public class AllTests : Db4oTestSuite
	{
		protected override Type[] TestCases()
		{
			return new Type[] { typeof(GenericReflectorStateTest), typeof(NoTestConstructorsTestCase
				), typeof(ReflectArrayTestCase), typeof(Db4objects.Db4o.Tests.Common.Reflect.Custom.AllTests
				), typeof(GenericObjectsTest) };
		}

		public static void Main(string[] args)
		{
			new Db4objects.Db4o.Tests.Common.Reflect.AllTests().RunSolo();
		}
	}
}
