namespace Db4objects.Drs.Test
{
	public class Db4oListTest : Db4objects.Drs.Test.ListTest
	{
		public override void Test()
		{
			if (!(A().Provider() is Db4objects.Drs.Db4o.Db4oReplicationProvider))
			{
				return;
			}
			base.ActualTest();
		}

		protected override Db4objects.Drs.Test.ListHolder CreateHolder()
		{
			Db4objects.Drs.Test.ListHolder lh = new Db4objects.Drs.Test.ListHolder("h1");
			Db4objects.Db4o.Types.IDb4oList list = ((Db4objects.Drs.Db4o.Db4oReplicationProvider
				)A().Provider()).GetObjectContainer().Collections().NewLinkedList();
			lh.SetList(list);
			return lh;
		}
	}
}
