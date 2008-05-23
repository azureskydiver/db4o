/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using System.Collections;
using Db4oUnit;
using Db4oUnit.Fixtures;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Reflect;
using Db4objects.Drs.Tests;

namespace Db4objects.Drs.Tests
{
	public class ArrayTestSuite : FixtureBasedTestSuite
	{
		public class Item
		{
			public object array;

			public Item(object array_)
			{
				array = array_;
			}
		}

		public class TestUnit : DrsTestCase
		{
			public virtual void Test()
			{
				ArrayTestSuite.Item item = new ArrayTestSuite.Item(Subject());
				StoreToProviderA(item);
				ReplicatedAllToB();
				ArrayTestSuite.Item replicated = ReplicatedItem();
				Assert.IsNotNull(replicated.array);
				Iterator4Assert.AreEqual(ArrayIterator(item.array), ArrayIterator(replicated.array
					));
			}

			private IEnumerator ArrayIterator(object array)
			{
				return ArrayHandler.Iterator(ReflectClass(array), array);
			}

			private IReflectClass ReflectClass(object array)
			{
				return GenericReflector().ForObject(array);
			}

			private Db4objects.Db4o.Reflect.Generic.GenericReflector GenericReflector()
			{
				return new Db4objects.Db4o.Reflect.Generic.GenericReflector(null, Platform4.ReflectorForType
					(GetType()));
			}

			private void ReplicatedAllToB()
			{
				ReplicateAll(A().Provider(), B().Provider());
			}

			private void StoreToProviderA(ArrayTestSuite.Item item)
			{
				A().Provider().StoreNew(item);
				A().Provider().Commit();
			}

			private ArrayTestSuite.Item ReplicatedItem()
			{
				IEnumerator iterator = B().Provider().GetStoredObjects(typeof(ArrayTestSuite.Item
					)).GetEnumerator();
				if (iterator.MoveNext())
				{
					return (ArrayTestSuite.Item)iterator.Current;
				}
				return null;
			}

			private object Subject()
			{
				return SubjectFixtureProvider.Value();
			}
		}

		public override IFixtureProvider[] FixtureProviders()
		{
			return new IFixtureProvider[] { new SubjectFixtureProvider(new object[] { new object
				[] {  }, new string[] { "foo", "bar" }, new int[] { 42, -1, 0 }, new int[][] {  }
				, new DateTime[] { new DateTime() } }) };
		}

		public override Type[] TestUnits()
		{
			return new Type[] { typeof(ArrayTestSuite.TestUnit) };
		}
	}
}
