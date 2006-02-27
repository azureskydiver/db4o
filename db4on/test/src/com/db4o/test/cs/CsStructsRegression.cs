using System;
using com.db4o.query;

namespace com.db4o.test.cs
{
	public class CsStructsRegression
	{
		public void store()
		{
			Tester.store(new Item());
			Tester.store(new Item(1));
			Tester.store(new Item(2));
		}

		public void testConstrainOnNullableValue()
		{
			checkQueryById(1);
			checkQueryById(2);
		}

		private static void checkQueryById(int id)
		{
			ObjectSet os = queryById(id);
			Tester.ensureEquals(1, os.size());
			Tester.ensureEquals(id, ((Item)os.next()).Id);
		}

		private static ObjectSet queryById(int id)
		{
			Query q = Tester.query();
			q.constrain(typeof(Item));
			q.descend("_id").descend("_value").constrain(id);
			return q.execute();
		}
	}

	public class Item
	{
		NullableInt32 _id;

		public Item(int id)
		{
			_id = new NullableInt32(id);
		}

		public Item()
		{	
		}

		public int Id
		{
			get
			{
				return _id.Value;
			}
		}
	}

	public struct NullableInt32
	{
		private int _value;
		private bool _hasValue;

		public NullableInt32(int value)
		{
			_value = value;
			_hasValue = true;
		}

		public int Value
		{
			get { return _value; }
		}

		public bool HasValue
		{
			get { return _hasValue; }
		}
	}
}
