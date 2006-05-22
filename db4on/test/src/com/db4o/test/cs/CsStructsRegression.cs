using System;
using com.db4o.query;

namespace com.db4o.test.cs
{
	public class CsStructsRegression
	{
		public void Store()
		{
			Tester.Store(new Item());
			Tester.Store(new Item(1));
			Tester.Store(new Item(2));
		}

		public void TestConstrainOnNullableValue()
		{
			CheckQueryById(1);
			CheckQueryById(2);
		}

		private static void CheckQueryById(int id)
		{
			ObjectSet os = QueryById(id);
			Tester.EnsureEquals(1, os.Size());
			Tester.EnsureEquals(id, ((Item)os.Next()).Id);
		}

		private static ObjectSet QueryById(int id)
		{
			Query q = Tester.Query();
			q.Constrain(typeof(Item));
			q.Descend("_id").Descend("_value").Constrain(id);
			return q.Execute();
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
