using System;
using System.Diagnostics;
/* Copyright (C) 2007   Versant Inc.   http://www.db4o.com */
#if !CF && !SILVERLIGHT
using Db4objects.Db4o.Monitoring.Internal;
using Db4oUnit;
using Db4oUnit.Extensions;
#if CF_3_5 || NET_3_5
using System.Linq;
using Db4objects.Db4o.Linq;
#endif

namespace Db4objects.Db4o.Tests.Monitoring
{
	internal class QueryMonitoringSupportTestCaseBase : AbstractDb4oTestCase
	{
		protected override void Db4oSetupBeforeConfigure()
		{
			Db4oPerformanceCounterCategory.ReInstall();
		}

		protected void ExecuteOptimizedLinq()
		{
			var found = (from Item item in Db()
			             where item.id == 42
			             select item).ToArray();
		}

		protected void ExecuteUnoptimizedLinq()
		{
			var found = (from Item item in Db()
			             where item.GetType() == typeof(Item)
			             select item).ToArray();
		}

		protected void AssertCounter(PerformanceCounter performanceCounter, Action action)
		{
			using (PerformanceCounter counter = performanceCounter)
			{
				Assert.AreEqual(0, counter.RawValue);

				for (int i = 0; i < 3; ++i)
				{
					action();
					Assert.AreEqual(i + 1, counter.RawValue);
				}
			}
		}

		protected void ExecuteOptimizedNQ()
		{
			Db().Query(delegate(Item item) { return item.id == 42; });
		}

		protected void ExecuteUnoptimizedNQ()
		{
			Db().Query(delegate(Item item) { return item.GetType() == typeof (Item); });
		}

		public class Item
		{
			public int id;
		}
	}
}
#endif