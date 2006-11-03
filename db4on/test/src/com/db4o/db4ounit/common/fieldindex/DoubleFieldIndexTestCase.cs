namespace com.db4o.db4ounit.common.fieldindex
{
	/// <exclude></exclude>
	public class DoubleFieldIndexTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase().RunSolo();
		}

		public class Item
		{
			public double value;

			public Item()
			{
			}

			public Item(double value_)
			{
				value = value_;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			IndexField(config, typeof(com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase.Item)
				, "value");
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase.Item(0.5
				));
			Db().Set(new com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase.Item(1.1
				));
			Db().Set(new com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase.Item(2)
				);
		}

		public virtual void TestEqual()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase.Item)
				);
			query.Descend("value").Constrain(1.1);
			AssertItems(new double[] { 1.1 }, query.Execute());
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase.Item)
				);
			com.db4o.query.Query descend = query.Descend("value");
			descend.Constrain(System.Convert.ToDouble(1)).Greater();
			descend.OrderAscending();
			AssertItems(new double[] { 1.1, 2 }, query.Execute());
		}

		private void AssertItems(double[] expected, com.db4o.ObjectSet set)
		{
			Db4oUnit.ArrayAssert.AreEqual(expected, ToDoubleArray(set));
		}

		private double[] ToDoubleArray(com.db4o.ObjectSet set)
		{
			double[] array = new double[set.Size()];
			for (int i = 0; i < array.Length; i++)
			{
				array[i] = ((com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase.Item)set
					.Next()).value;
			}
			return array;
		}
	}
}
