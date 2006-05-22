namespace com.db4o.test.net2
{
#if NET_2_0 || CF_2_0
    using System;
	using System.Text;
	using com.db4o;
	using com.db4o.test;
	using com.db4o.query;

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
		public void StoreOne()
		{
			Tester.Store(new SimpleGenericType<string>("Will it work?"));
			Tester.Store(new SimpleGenericType<int>(42));
		}

		public void Test()
		{
			TstGenericType("Will it work?");
			TstGenericType(42);
		}

		private void TstGenericType<T>(T expectedValue)
		{
			Query query = Tester.Query();
			query.Constrain(typeof(SimpleGenericType<T>));

			EnsureGenericItem<T>(expectedValue, query.Execute());

			query = Tester.Query();
			query.Constrain(typeof(SimpleGenericType<T>));
			query.Descend("value").Constrain(expectedValue);
			EnsureGenericItem<T>(expectedValue, query.Execute());
		}

		private static void EnsureGenericItem<T>(T expectedValue, ObjectSet os)
		{
			Tester.EnsureEquals(1, os.Size());

			SimpleGenericType<T> item = (SimpleGenericType<T>)os.Next();
			Tester.EnsureEquals(expectedValue, item.value);
		}
	}
#endif
}
