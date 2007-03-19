namespace Db4objects.Drs.Test
{
    public class ReplicationTraversalTest : Db4objects.Drs.Test.DrsTestCase
    {

        public virtual void Test()
        {
            Db4objects.Drs.Test.Replicated obj1 = new Db4objects.Drs.Test.Replicated("1");
            Db4objects.Drs.Test.Replicated obj2 = new Db4objects.Drs.Test.Replicated("2");
            Db4objects.Drs.Test.Replicated obj3 = new Db4objects.Drs.Test.Replicated("3");
            obj1.SetLink(obj2);
            obj2.SetLink(obj3);
            obj3.SetLink(obj1);
            A().Provider().StoreNew(obj1);
            ReplicateClass(A().Provider(), B().Provider(), typeof(Replicated));
            EnsureContains(A().Provider(), obj1);
            EnsureContains(A().Provider(), obj2);
            EnsureContains(A().Provider(), obj3);
        }

        protected virtual void EnsureContains(Db4objects.Drs.Inside.ITestableReplicationProviderInside provider, Replicated obj)
        {
            Db4objects.Db4o.IObjectSet objectSet = provider.GetStoredObjects(typeof(Replicated));
            Db4oUnit.Assert.IsTrue(objectSet.Contains(obj));
        }
    }
}
