namespace com.db4o.drs.test
{
    public class ReplicationTraversalTest : com.db4o.drs.test.DrsTestCase
    {

        public virtual void Test()
        {
            com.db4o.drs.test.Replicated obj1 = new com.db4o.drs.test.Replicated("1");
            com.db4o.drs.test.Replicated obj2 = new com.db4o.drs.test.Replicated("2");
            com.db4o.drs.test.Replicated obj3 = new com.db4o.drs.test.Replicated("3");
            obj1.SetLink(obj2);
            obj2.SetLink(obj3);
            obj3.SetLink(obj1);
            A().Provider().StoreNew(obj1);
            ReplicateClass(A().Provider(), B().Provider(), typeof(Replicated));
            EnsureContains(A().Provider(), obj1);
            EnsureContains(A().Provider(), obj2);
            EnsureContains(A().Provider(), obj3);
        }

        protected virtual void EnsureContains(com.db4o.drs.inside.TestableReplicationProviderInside provider, Replicated obj)
        {
            ObjectSet objectSet = provider.GetStoredObjects(typeof(Replicated));
            Db4oUnit.Assert.IsTrue(objectSet.Contains(obj));
        }
    }
}
