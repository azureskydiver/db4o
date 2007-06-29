/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4oUnit;
using Db4oUnit.Extensions;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Handlers;

namespace Db4objects.Db4o.Tests.Common.Handlers
{
	/// <exclude></exclude>
	public class DoubleHandlerTestCase : AbstractDb4oTestCase
	{
		private IIndexable4 _handler;

		protected override void Db4oSetupBeforeStore()
		{
			_handler = new DoubleHandler(Stream());
		}

		public virtual void TestMarshalling()
		{
			double expected = 1.1;
			Db4objects.Db4o.Internal.Buffer buffer = new Db4objects.Db4o.Internal.Buffer(_handler
				.LinkLength());
			_handler.WriteIndexEntry(buffer, expected);
			buffer.Seek(0);
			object actual = _handler.ReadIndexEntry(buffer);
			Assert.AreEqual(expected, actual);
		}

		public virtual void TestComparison()
		{
			AssertComparison(0, 1.1, 1.1);
			AssertComparison(1, 1.0, 1.1);
			AssertComparison(-1, 1.1, 0.5);
		}

		private void AssertComparison(int expected, double prepareWith, double compareTo)
		{
			_handler.PrepareComparison(prepareWith);
			double doubleCompareTo = compareTo;
			Assert.AreEqual(expected, _handler.CompareTo(doubleCompareTo));
		}
	}
}
