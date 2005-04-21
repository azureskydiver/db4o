#if NET_2_0
using System;
using System.Text;
using com.db4o;
using com.db4o.test;
using com.db4o.query;

namespace com.db4o.test.net2
{
	class SimpleGenericType<T>
	{
		public T value;

		public SimpleGenericType(T value)
		{
			this.value = value;
		}
	}

	class Net2SimpleGenericType
	{
		public void storeOne()
		{
			Test.store(new SimpleGenericType<string>("Will it work?"));
			Test.store(new SimpleGenericType<int>(42));
		}

		public void test()
		{
			tstGenericType("Will it work?");
			tstGenericType(42);
		}

		private void tstGenericType<T>(T expectedValue)
		{
			Query query = Test.query();
			query.constrain(typeof(SimpleGenericType<T>));

			ensureGenericItem<T>(expectedValue, query.execute());

			query = Test.query();
			query.constrain(typeof(SimpleGenericType<T>));
			query.descend("value").constrain(expectedValue);
			ensureGenericItem<T>(expectedValue, query.execute());
		}

		private static void ensureGenericItem<T>(T expectedValue, ObjectSet os)
		{
			Test.ensureEquals(1, os.size());

			SimpleGenericType<T> item = (SimpleGenericType<T>)os.next();
			Test.ensureEquals(expectedValue, item.value);
		}
	}
}
#endif