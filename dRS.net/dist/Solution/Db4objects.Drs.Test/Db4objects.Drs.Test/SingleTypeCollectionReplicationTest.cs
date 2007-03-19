namespace Db4objects.Drs.Test
{
    public class SingleTypeCollectionReplicationTest : Db4objects.Drs.Test.DrsTestCase
    {
        protected virtual void ActualTest()
        {
            Db4objects.Drs.Test.CollectionHolder h1 = new Db4objects.Drs.Test.CollectionHolder();
            h1.ht["1"] = "One";
            h1.set["2"] = "two";
            h1.list.Add("three");
            A().Provider().StoreNew(h1);
            A().Provider().Commit();
            Db4objects.Drs.IReplicationSession replication = new Db4objects.Drs.Inside.GenericReplicationSession(A().Provider(), B().Provider());
            Db4objects.Db4o.IObjectSet objectSet = A().Provider().ObjectsChangedSinceLastReplication();
            while (objectSet.HasNext())
            {
                replication.Replicate(objectSet.Next());
            }
            replication.Commit();
            Db4objects.Db4o.IObjectSet it = B().Provider().GetStoredObjects(typeof(Db4objects.Drs.Test.CollectionHolder));
            Db4oUnit.Assert.IsTrue(it.HasNext());
            Db4objects.Drs.Test.CollectionHolder replica = (Db4objects.Drs.Test.CollectionHolder)it.Next();
            Db4oUnit.Assert.AreEqual("One", replica.ht["1"]);
            Db4oUnit.Assert.IsTrue(replica.set.Contains("2"));
            Db4oUnit.Assert.AreEqual("three", replica.list[0]);
        }

        public virtual void Test()
        {
            ActualTest();
        }
    }
}
