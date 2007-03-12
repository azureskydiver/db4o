namespace com.db4o.db4ounit.common.assorted
{
	public class MaximumActivationDepthTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Data
		{
			public int _id;

			public com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data _prev;

			public Data(int id, com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data
				 prev)
			{
				_id = id;
				_prev = prev;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ActivationDepth(int.MaxValue);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data)
				).MaximumActivationDepth(1);
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data data = new 
				com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data(2, null);
			data = new com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data(
				1, data);
			data = new com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data(
				0, data);
			Store(data);
		}

		public virtual void TestActivationRestricted()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data)
				);
			query.Descend("_id").Constrain(0);
			com.db4o.ObjectSet result = query.Execute();
			Db4oUnit.Assert.AreEqual(1, result.Size());
			com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data data = (com.db4o.db4ounit.common.assorted.MaximumActivationDepthTestCase.Data
				)result.Next();
			Db4oUnit.Assert.IsNotNull(data._prev);
			Db4oUnit.Assert.IsNull(data._prev._prev);
		}
	}
}
