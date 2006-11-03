namespace com.db4o.db4ounit.common.soda.util
{
	public abstract class SodaBaseTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		[com.db4o.Transient]
		protected object[] _array;

		protected override void Db4oSetupBeforeStore()
		{
			_array = CreateData();
		}

		protected override void Store()
		{
			object[] data = CreateData();
			for (int idx = 0; idx < data.Length; idx++)
			{
				Db().Set(data[idx]);
			}
		}

		public abstract object[] CreateData();

		protected virtual void Expect(com.db4o.query.Query query, int[] indices)
		{
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(query, CollectCandidates(indices
				), false);
		}

		protected virtual void ExpectOrdered(com.db4o.query.Query query, int[] indices)
		{
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOrdered(query, CollectCandidates
				(indices));
		}

		private object[] CollectCandidates(int[] indices)
		{
			object[] data = new object[indices.Length];
			for (int idx = 0; idx < indices.Length; idx++)
			{
				data[idx] = _array[indices[idx]];
			}
			return data;
		}
	}
}
