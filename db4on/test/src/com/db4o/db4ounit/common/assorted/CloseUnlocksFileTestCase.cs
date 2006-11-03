namespace com.db4o.db4ounit.common.assorted
{
	public class CloseUnlocksFileTestCase : Db4oUnit.TestCase
	{
		private static readonly string FILE = "unlocked.db4o";

		public virtual void Test()
		{
			com.db4o.db4ounit.util.File4.Delete(FILE);
			Db4oUnit.Assert.IsFalse(System.IO.File.Exists(FILE));
			com.db4o.ObjectContainer oc = com.db4o.Db4o.OpenFile(FILE);
			oc.Close();
			com.db4o.db4ounit.util.File4.Delete(FILE);
			Db4oUnit.Assert.IsFalse(System.IO.File.Exists(FILE));
		}
	}
}
