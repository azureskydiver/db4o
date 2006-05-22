using System;
using System.Collections;

namespace com.db4o.test
{
	/// <summary>
	/// Tests ObjectSet's IList functionality.
	/// </summary>
	public class ObjectSetAsList
	{	
		int _value;

		public ObjectSetAsList()
		{	
		}

		public ObjectSetAsList(int value)
		{
			_value = value;
		}

		public override bool Equals(object other)
		{
			ObjectSetAsList rhs = other as ObjectSetAsList;
			return null == rhs ? false : rhs._value == _value;
		}

		public int Value
		{
			get { return _value; }
		}

		public void Store()
		{
			Tester.Store(new ObjectSetAsList(42));
			Tester.Store(new ObjectSetAsList(1));
		}

		public void TestEnumerable()
		{
			ObjectSet os = GetObjectSet();
			for (int i=0; i<2; ++i)
			{
				int sum = 0;
				foreach (ObjectSetAsList item in os)
				{
					sum += item.Value;
				}
				Tester.EnsureEquals(43, sum);
			}
		}

		public void TestList()
		{
			IList os = GetObjectSet();
			Tester.EnsureEquals(2, os.Count);

			int sum = 0;
			for (int i=0; i<os.Count; ++i)
			{
				ObjectSetAsList item = (ObjectSetAsList)os[i];
				sum += item.Value;
			}
			Tester.EnsureEquals(43, sum);
		}
		
		public void TestIndexOfAndContains()
		{
			IList os = GetObjectSet();

			Tester.EnsureEquals(0, os.IndexOf(os[0]));
			Tester.EnsureEquals(1, os.IndexOf(os[1]));
			Tester.Ensure(os.Contains(os[0]));
			Tester.Ensure(os.Contains(os[1]));
			Tester.Ensure("Contains is not by value", !os.Contains(new ObjectSetAsList(42)));
		}

		public void TestAccessOrder() 
		{
			ObjectSet os = GetObjectSet();
			for(int i=0;i<os.Count;i++) {
				Tester.Ensure(os.HasNext());
				Tester.Ensure(os.Next()==os[i]);
			}
			Tester.Ensure(!os.HasNext());
		}

		public void TestCopyTo()
		{
			IList os = GetObjectSet();
			ObjectSetAsList[] items = new ObjectSetAsList[2];
			os.CopyTo(items, 0);
			Tester.EnsureEquals(items[0], os[0]);
			Tester.EnsureEquals(items[1], os[1]);
		}

		private ObjectSet GetObjectSet()
		{
			return Tester.ObjectContainer().Get(typeof(ObjectSetAsList));
		}
	}
}
