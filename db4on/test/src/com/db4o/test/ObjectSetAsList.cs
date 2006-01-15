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

		public void store()
		{
			Tester.store(new ObjectSetAsList(42));
			Tester.store(new ObjectSetAsList(1));
		}

		public void testEnumerable()
		{
			ObjectSet os = getObjectSet();
			for (int i=0; i<2; ++i)
			{
				int sum = 0;
				foreach (ObjectSetAsList item in os)
				{
					sum += item.Value;
				}
				Tester.ensureEquals(43, sum);
			}
		}

		public void testList()
		{
			IList os = getObjectSet();
			Tester.ensureEquals(2, os.Count);

			int sum = 0;
			for (int i=0; i<os.Count; ++i)
			{
				ObjectSetAsList item = (ObjectSetAsList)os[i];
				sum += item.Value;
			}
			Tester.ensureEquals(43, sum);
		}
		
		public void testIndexOfAndContains()
		{
			IList os = getObjectSet();

			Tester.ensureEquals(0, os.IndexOf(os[0]));
			Tester.ensureEquals(1, os.IndexOf(os[1]));
			Tester.ensure(os.Contains(os[0]));
			Tester.ensure(os.Contains(os[1]));
			Tester.ensure("Contains is not by value", !os.Contains(new ObjectSetAsList(42)));
		}

		public void testAccessOrder() 
		{
			ObjectSet os = getObjectSet();
			for(int i=0;i<os.Count;i++) {
				Tester.ensure(os.hasNext());
				Tester.ensure(os.next()==os[i]);
			}
			Tester.ensure(!os.hasNext());
		}

		public void testCopyTo()
		{
			IList os = getObjectSet();
			ObjectSetAsList[] items = new ObjectSetAsList[2];
			os.CopyTo(items, 0);
			Tester.ensureEquals(items[0], os[0]);
			Tester.ensureEquals(items[1], os[1]);
		}

		private ObjectSet getObjectSet()
		{
			return Tester.objectContainer().get(typeof(ObjectSetAsList));
		}
	}
}
