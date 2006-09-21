namespace com.db4o.drs.test
{
	public class Db4oListTest : com.db4o.drs.test.ListTest
	{
		public override void Test()
		{
			if (!(A().Provider() is com.db4o.drs.db4o.Db4oReplicationProvider))
			{
				return;
			}
			base.ActualTest();
		}

		protected override com.db4o.drs.test.ListHolder CreateHolder()
		{
			com.db4o.drs.test.ListHolder lh = new com.db4o.drs.test.ListHolder("h1");
			com.db4o.types.Db4oList list = ((com.db4o.drs.db4o.Db4oReplicationProvider)A().Provider
				()).GetObjectContainer().Collections().NewLinkedList();
			lh.SetList(list);
			return lh;
		}
	}
}
