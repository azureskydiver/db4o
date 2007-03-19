using System.Collections;

namespace Db4objects.Drs.Test
{
    public class R0to4Runner : Db4objects.Drs.Test.DrsTestCase
    {
        private const int LINKERS = 4;

        public R0to4Runner()
            : base()
        {
        }

        protected override void Clean()
        {
            Delete(A().Provider());
            Delete(B().Provider());
        }

        protected virtual void Delete(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             provider)
        {
            IDictionary toDelete = new Hashtable();

            //			j4o.util.Set toDelete = new j4o.util.HashSet();
            Db4objects.Db4o.IObjectSet rr = provider.GetStoredObjects(typeof(Db4objects.Drs.Test.R0));
            while (rr.HasNext())
            {
                object o = rr.Next();
                Db4objects.Db4o.Reflect.IReflectClass claxx = Db4objects.Drs.Inside.ReplicationReflector.GetInstance
                    ().Reflector().ForObject(o);
                SetFieldsToNull(o, claxx);
                toDelete.Add(o.ToString(), o);
            }
            for (System.Collections.IEnumerator iterator = toDelete.GetEnumerator(); iterator
                .MoveNext(); )
            {
                object o = iterator.Current;
                provider.Delete(o);
            }
            provider.Commit();
        }

        private void CompareR4(Db4objects.Drs.Inside.ITestableReplicationProviderInside a, Db4objects.Drs.Inside.ITestableReplicationProviderInside
             b, bool isSameExpected)
        {
            Db4objects.Db4o.IObjectSet it = a.GetStoredObjects(typeof(Db4objects.Drs.Test.R4));
            while (it.HasNext())
            {
                string name = ((Db4objects.Drs.Test.R4)it.Next()).name;
                Db4objects.Db4o.IObjectSet it2 = b.GetStoredObjects(typeof(Db4objects.Drs.Test.R4));
                bool found = false;
                while (it2.HasNext())
                {
                    string name2 = ((Db4objects.Drs.Test.R4)it2.Next()).name;
                    if (name.Equals(name2))
                    {
                        found = true;
                    }
                }
                Db4oUnit.Assert.IsTrue(found == isSameExpected);
            }
        }

        private void CopyAllToB(Db4objects.Drs.Inside.ITestableReplicationProviderInside peerA
            , Db4objects.Drs.Inside.ITestableReplicationProviderInside peerB)
        {
            Db4oUnit.Assert.IsTrue(ReplicateAll(peerA, peerB, false) == LINKERS * 5);
        }

        private void EnsureCount(Db4objects.Drs.Inside.ITestableReplicationProviderInside provider
            , int linkers)
        {
            EnsureCount(provider, typeof(Db4objects.Drs.Test.R0), linkers * 5);
            EnsureCount(provider, typeof(Db4objects.Drs.Test.R1), linkers * 4);
            EnsureCount(provider, typeof(Db4objects.Drs.Test.R2), linkers * 3);
            EnsureCount(provider, typeof(Db4objects.Drs.Test.R3), linkers * 2);
            EnsureCount(provider, typeof(Db4objects.Drs.Test.R4), linkers * 1);
        }

        private void EnsureCount(Db4objects.Drs.Inside.ITestableReplicationProviderInside provider
            , System.Type clazz, int count)
        {
            Db4objects.Db4o.IObjectSet instances = provider.GetStoredObjects(clazz);
            int i = count;
            while (instances.HasNext())
            {
                instances.Next();
                i--;
            }
            Db4oUnit.Assert.IsTrue(i == 0);
        }

        private void EnsureR4Different(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             peerA, Db4objects.Drs.Inside.ITestableReplicationProviderInside peerB)
        {
            CompareR4(peerB, peerA, false);
        }

        private void EnsureR4Same(Db4objects.Drs.Inside.ITestableReplicationProviderInside peerA
            , Db4objects.Drs.Inside.ITestableReplicationProviderInside peerB)
        {
            CompareR4(peerB, peerA, true);
            CompareR4(peerA, peerB, true);
        }

        private void Init(Db4objects.Drs.Inside.ITestableReplicationProviderInside peerA)
        {
            Db4objects.Drs.Test.R0Linker lCircles = new Db4objects.Drs.Test.R0Linker();
            lCircles.SetNames("circles");
            lCircles.LinkCircles();
            lCircles.Store(peerA);
            Db4objects.Drs.Test.R0Linker lList = new Db4objects.Drs.Test.R0Linker();
            lList.SetNames("list");
            lList.LinkList();
            lList.Store(peerA);
            Db4objects.Drs.Test.R0Linker lThis = new Db4objects.Drs.Test.R0Linker();
            lThis.SetNames("this");
            lThis.LinkThis();
            lThis.Store(peerA);
            Db4objects.Drs.Test.R0Linker lBack = new Db4objects.Drs.Test.R0Linker();
            lBack.SetNames("back");
            lBack.LinkBack();
            lBack.Store(peerA);
            peerA.Commit();
        }

        private void ModifyR4(Db4objects.Drs.Inside.ITestableReplicationProviderInside provider
            )
        {
            Db4objects.Db4o.IObjectSet it = provider.GetStoredObjects(typeof(Db4objects.Drs.Test.R4));
            while (it.HasNext())
            {
                Db4objects.Drs.Test.R4 r4 = (Db4objects.Drs.Test.R4)it.Next();
                r4.name = r4.name + "_";
                provider.Update(r4);
            }
            provider.Commit();
        }

        private int Replicate(Db4objects.Drs.Inside.ITestableReplicationProviderInside peerA
            , Db4objects.Drs.Inside.ITestableReplicationProviderInside peerB)
        {
            return ReplicateAll(peerA, peerB, true);
        }

        private int ReplicateAll(Db4objects.Drs.Inside.ITestableReplicationProviderInside peerA
            , Db4objects.Drs.Inside.ITestableReplicationProviderInside peerB, bool modifiedOnly
            )
        {
            Db4objects.Drs.IReplicationSession replication = Db4objects.Drs.Replication.Begin(peerA
                , peerB);
            Db4objects.Db4o.IObjectSet it = modifiedOnly ? peerA.ObjectsChangedSinceLastReplication(typeof(
                Db4objects.Drs.Test.R0)) : peerA.GetStoredObjects(typeof(Db4objects.Drs.Test.R0));
            int replicated = 0;
            while (it.HasNext())
            {
                Db4objects.Drs.Test.R0 r0 = (Db4objects.Drs.Test.R0)it.Next();
                replication.Replicate(r0);
                replicated++;
            }
            replication.Commit();
            EnsureCount(peerA, LINKERS);
            EnsureCount(peerB, LINKERS);
            return replicated;
        }

        private void ReplicateNoneModified(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             peerA, Db4objects.Drs.Inside.ITestableReplicationProviderInside peerB)
        {
            Db4oUnit.Assert.IsTrue(Replicate(peerA, peerB) == 0);
        }

        private void ReplicateR4(Db4objects.Drs.Inside.ITestableReplicationProviderInside peerA
            , Db4objects.Drs.Inside.ITestableReplicationProviderInside peerB)
        {
            int replicatedObjectsCount = ReplicateAll(peerA, peerB, true);
            Db4oUnit.Assert.IsTrue(replicatedObjectsCount == LINKERS);
        }

        private void SetFieldsToNull(object _object, Db4objects.Db4o.Reflect.IReflectClass claxx)
        {
            Db4objects.Db4o.Reflect.IReflectField[] fields;
            fields = claxx.GetDeclaredFields();
            for (int i = 0; i < fields.Length; i++)
            {
                Db4objects.Db4o.Reflect.IReflectField field = fields[i];
                if (field.IsStatic())
                {
                    continue;
                }
                if (field.IsTransient())
                {
                    continue;
                }
                field.SetAccessible();
                field.Set(_object, null);
            }
            Db4objects.Db4o.Reflect.IReflectClass superclass = claxx.GetSuperclass();
            if (superclass == null)
            {
                return;
            }
            SetFieldsToNull(_object, superclass);
        }

        public virtual void Test()
        {
            ActualTest();
        }

        protected virtual void ActualTest()
        {
            Init(A().Provider());
            EnsureCount(A().Provider(), LINKERS);
            CopyAllToB(A().Provider(), B().Provider());
            ReplicateNoneModified(A().Provider(), B().Provider());
            ModifyR4(A().Provider());
            EnsureR4Different(A().Provider(), B().Provider());
            ReplicateR4(A().Provider(), B().Provider());
            EnsureR4Same(A().Provider(), B().Provider());
        }
    }
}
