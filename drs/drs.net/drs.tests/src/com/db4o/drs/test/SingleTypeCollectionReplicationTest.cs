namespace com.db4o.drs.test
{
    public class SingleTypeCollectionReplicationTest : com.db4o.drs.test.DrsTestCase
    {
        protected virtual void ActualTest()
        {
            com.db4o.drs.test.CollectionHolder h1 = new com.db4o.drs.test.CollectionHolder();
            h1.ht["1"] = "One";
            h1.set["2"] = "two";
            h1.list.Add("three");
            A().Provider().StoreNew(h1);
            A().Provider().Commit();
            com.db4o.drs.ReplicationSession replication = new com.db4o.drs.inside.GenericReplicationSession(A().Provider(), B().Provider());
            com.db4o.ObjectSet objectSet = A().Provider().ObjectsChangedSinceLastReplication();
            while (objectSet.HasNext())
            {
                replication.Replicate(objectSet.Next());
            }
            replication.Commit();
            com.db4o.ObjectSet it = B().Provider().GetStoredObjects(typeof(com.db4o.drs.test.CollectionHolder));
            Db4oUnit.Assert.IsTrue(it.HasNext());
            com.db4o.drs.test.CollectionHolder replica = (com.db4o.drs.test.CollectionHolder)it.Next();
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
