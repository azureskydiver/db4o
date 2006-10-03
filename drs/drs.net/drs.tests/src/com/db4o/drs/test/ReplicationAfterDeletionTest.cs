namespace com.db4o.drs.test
{
	public class ReplicationAfterDeletionTest : com.db4o.drs.test.DrsTestCase
	{
		public virtual void Test()
		{
			Replicate();
			Clean();
			Replicate();
			Clean();
		}

		protected override void Clean()
		{
			Delete(new System.Type[] { typeof(com.db4o.drs.test.SPCChild), typeof(com.db4o.drs.test.SPCParent
				) });
		}

		private void Replicate()
		{
			com.db4o.drs.test.SPCChild child = new com.db4o.drs.test.SPCChild("c1");
			com.db4o.drs.test.SPCParent parent = new com.db4o.drs.test.SPCParent(child, "p1");
			A().Provider().StoreNew(parent);
			A().Provider().Commit();
			ReplicateAll(A().Provider(), B().Provider());
		}
	}
}
