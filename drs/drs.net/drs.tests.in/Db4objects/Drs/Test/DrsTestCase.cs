namespace Db4objects.Drs.Test
{
    public abstract class DrsTestCase : Db4oUnit.ITestCase, Db4oUnit.ITestLifeCycle
    {
        public static readonly System.Type[] mappings;

        public static readonly System.Type[] extraMappingsForCleaning = new System.Type[] { typeof(System.Collections.IDictionary), typeof(System.Collections.IList) };

        static DrsTestCase()
        {
            mappings = new System.Type[] { typeof(SPCParent), 
                                           typeof(SPCChild), 
                                           typeof(SimpleArrayContent), 
                                           typeof(SimpleArrayHolder), 
                                           typeof(Pilot), 
                                           typeof(Car),
                                           typeof(R0),
                                           typeof(R1),
                                           typeof(R2),
                                           typeof(R3),
                                           typeof(R4),
                                         typeof(Replicated)};
        }

        private Db4objects.Drs.Test.IDrsFixture _a;

        private Db4objects.Drs.Test.IDrsFixture _b;

        public virtual void SetUp()
        {
            CleanBoth();
            Configure();
            OpenBoth();
            Store();
            Reopen();
        }

        private void CleanBoth()
        {
            _a.Clean();
            _b.Clean();
        }

        protected virtual void Clean()
        {
            for (int i = 0; i < mappings.Length; i++)
            {
                A().Provider().DeleteAllInstances(mappings[i]);
                B().Provider().DeleteAllInstances(mappings[i]);
            }
            for (int i = 0; i < extraMappingsForCleaning.Length; i++)
            {
                A().Provider().DeleteAllInstances(extraMappingsForCleaning[i]);
                B().Provider().DeleteAllInstances(extraMappingsForCleaning[i]);
            }
            A().Provider().Commit();
            B().Provider().Commit();
        }

        protected virtual void Store()
        {
        }

        protected virtual void Configure()
        {
            Db4objects.Db4o.Db4o.Configure().GenerateUUIDs(int.MaxValue);
            Db4objects.Db4o.Db4o.Configure().GenerateVersionNumbers(int.MaxValue);
        }

        protected virtual void Reopen()
        {
            CloseBoth();
            OpenBoth();
        }

        private void OpenBoth()
        {
            _a.Open();
            _b.Open();
        }

        public virtual void TearDown()
        {
            CloseBoth();
            CleanBoth();
        }

        private void CloseBoth()
        {
            _a.Close();
            _b.Close();
        }

        public virtual void A(Db4objects.Drs.Test.IDrsFixture fixture)
        {
            _a = fixture;
        }

        public virtual void B(Db4objects.Drs.Test.IDrsFixture fixture)
        {
            _b = fixture;
        }

        public virtual Db4objects.Drs.Test.IDrsFixture A()
        {
            return _a;
        }

        public virtual Db4objects.Drs.Test.IDrsFixture B()
        {
            return _b;
        }

        protected virtual void EnsureOneInstance(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             provider, System.Type clazz)
        {
            EnsureInstanceCount(provider, clazz, 1);
        }

        protected virtual void EnsureInstanceCount(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             provider, System.Type clazz, int count)
        {
            Db4objects.Db4o.IObjectSet objectSet = provider.GetStoredObjects(clazz);
            Db4oUnit.Assert.AreEqual(count, objectSet.Size());
        }

        protected virtual object GetOneInstance(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             provider, System.Type clazz)
        {
            Db4objects.Db4o.IObjectSet objectSet = provider.GetStoredObjects(clazz);
            if (1 != objectSet.Size())
            {
                throw new System.Exception("Found more than one instance of + " + clazz
                    + " in provider = " + provider);
            }
            return objectSet.Next();
        }

        protected virtual void ReplicateAll(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             providerFrom, Db4objects.Drs.Inside.ITestableReplicationProviderInside providerTo)
        {
            Db4objects.Drs.IReplicationSession replication = Db4objects.Drs.Replication.Begin(providerFrom
                , providerTo);
            Db4objects.Db4o.IObjectSet allObjects = providerFrom.ObjectsChangedSinceLastReplication();
            if (!allObjects.HasNext())
            {
                throw new System.Exception("Can't find any objects to replicate");
            }
            while (allObjects.HasNext())
            {
                object changed = allObjects.Next();
                replication.Replicate(changed);
            }
            replication.Commit();
        }

        protected virtual void ReplicateAll(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             from, Db4objects.Drs.Inside.ITestableReplicationProviderInside to, Db4objects.Drs.IReplicationEventListener
             listener)
        {
            Db4objects.Drs.IReplicationSession replication = Db4objects.Drs.Replication.Begin(from
                , to, listener);
            Db4objects.Db4o.IObjectSet allObjects = from.ObjectsChangedSinceLastReplication();
            while (allObjects.HasNext())
            {
                object changed = allObjects.Next();
                replication.Replicate(changed);
            }
            replication.Commit();
        }

        protected virtual void Delete(System.Type[] classes)
        {
            for (int i = 0; i < classes.Length; i++)
            {
                A().Provider().DeleteAllInstances(classes[i]);
                B().Provider().DeleteAllInstances(classes[i]);
            }
            A().Provider().Commit();
            B().Provider().Commit();
        }

        protected virtual void ReplicateClass(Db4objects.Drs.Inside.ITestableReplicationProviderInside
             providerA, Db4objects.Drs.Inside.ITestableReplicationProviderInside providerB, System.Type
             clazz)
        {
            Db4objects.Drs.IReplicationSession replication = Db4objects.Drs.Replication.Begin(providerA
                , providerB);
            Db4objects.Db4o.IObjectSet allObjects = providerA.ObjectsChangedSinceLastReplication(clazz
                );
            while (allObjects.HasNext())
            {
                object obj = allObjects.Next();
                replication.Replicate(obj);
            }
            replication.Commit();
        }

        protected static void Sleep(int millis)
        {
            try
            {
                Sharpen.Lang.Thread.Sleep(millis);
            }
            catch (System.Exception e)
            {
                throw new System.Exception("exception",e);
            }
        }
    }
}
