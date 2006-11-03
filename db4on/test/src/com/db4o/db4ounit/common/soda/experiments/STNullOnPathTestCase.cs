namespace com.db4o.db4ounit.common.soda.experiments
{
	public class STNullOnPathTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public bool @bool;

		public STNullOnPathTestCase()
		{
		}

		public STNullOnPathTestCase(bool @bool)
		{
			this.@bool = @bool;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.experiments.STNullOnPathTestCase
				(false) };
		}

		public virtual void Test()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.experiments.STNullOnPathTestCase());
			q.Descend("bool").Constrain(null);
			Expect(q, new int[] {  });
		}
	}
}
