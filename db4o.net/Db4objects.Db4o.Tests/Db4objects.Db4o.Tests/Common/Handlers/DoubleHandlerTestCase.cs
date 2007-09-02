/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4oUnit;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Tests.Common.Handlers;

namespace Db4objects.Db4o.Tests.Common.Handlers
{
	/// <exclude></exclude>
	public class DoubleHandlerTestCase : TypeHandlerTestCaseBase
	{
		private IIndexable4 _handler;

		public static void Main(string[] args)
		{
			new DoubleHandlerTestCase().RunSolo();
		}

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

		public virtual void TestReadWrite()
		{
			MockWriteContext writeContext = new MockWriteContext(Db());
			DoubleHandler doubleHandler = (DoubleHandler)_handler;
			double expected = 1.23456789;
			doubleHandler.Write(writeContext, expected);
			MockReadContext readContext = new MockReadContext(writeContext);
			double d = (double)doubleHandler.Read(readContext);
			Assert.AreEqual(expected, d);
		}

		public virtual void TestStoreObject()
		{
			DoubleHandlerTestCase.Item storedItem = new DoubleHandlerTestCase.Item(1.023456789
				, 1.023456789);
			DoTestStoreObject(storedItem);
		}

		public class Item
		{
			public double _double;

			public double _doubleWrapper;

			public Item(double d, double wrapper)
			{
				_double = d;
				_doubleWrapper = wrapper;
			}

			public override bool Equals(object obj)
			{
				if (obj == this)
				{
					return true;
				}
				if (!(obj is DoubleHandlerTestCase.Item))
				{
					return false;
				}
				DoubleHandlerTestCase.Item other = (DoubleHandlerTestCase.Item)obj;
				return (other._double == this._double) && this._doubleWrapper.Equals(other._doubleWrapper
					);
			}

			public override string ToString()
			{
				return "[" + _double + "," + _doubleWrapper + "]";
			}
		}
	}
}
