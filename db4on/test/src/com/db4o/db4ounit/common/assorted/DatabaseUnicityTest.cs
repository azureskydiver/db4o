namespace com.db4o.db4ounit.common.assorted
{
	public class DatabaseUnicityTest : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public virtual void Test()
		{
			com.db4o.foundation.Hashtable4 ht = new com.db4o.foundation.Hashtable4();
			com.db4o.ext.ExtObjectContainer oc = Db();
			com.db4o.@internal.ObjectContainerBase yapStream = ((com.db4o.@internal.ObjectContainerBase
				)oc);
			yapStream.ShowInternalClasses(true);
			com.db4o.query.Query q = Db().Query();
			q.Constrain(typeof(com.db4o.ext.Db4oDatabase));
			com.db4o.ObjectSet objectSet = q.Execute();
			while (objectSet.HasNext())
			{
				com.db4o.ext.Db4oDatabase d4b = (com.db4o.ext.Db4oDatabase)objectSet.Next();
				Db4oUnit.Assert.IsFalse(ht.ContainsKey(d4b.i_signature));
				ht.Put(d4b.i_signature, string.Empty);
			}
			yapStream.ShowInternalClasses(false);
			oc.Close();
		}
	}
}
