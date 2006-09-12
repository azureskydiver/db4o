namespace com.db4o.test.replication.db4ounit
{
    public abstract class DrsTestCase : Db4oUnit.TestCase, Db4oUnit.TestLifeCycle
    {
        public static readonly System.Type[] mappings;

        public static readonly System.Type[] extraMappingsForCleaning = new System.Type[] { typeof(System.Collections.IDictionary), typeof(System.Collections.IList) };

        static DrsTestCase()
        {
            mappings = new System.Type[] { };
        }

        private com.db4o.test.replication.db4ounit.DrsFixture _a;

        private com.db4o.test.replication.db4ounit.DrsFixture _b;

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
            com.db4o.Db4o.Configure().GenerateUUIDs(int.MaxValue);
            com.db4o.Db4o.Configure().GenerateVersionNumbers(int.MaxValue);
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

        public virtual void A(com.db4o.test.replication.db4ounit.DrsFixture fixture)
        {
            _a = fixture;
        }

        public virtual void B(com.db4o.test.replication.db4ounit.DrsFixture fixture)
        {
            _b = fixture;
        }

        public virtual com.db4o.test.replication.db4ounit.DrsFixture A()
        {
            return _a;
        }

        public virtual com.db4o.test.replication.db4ounit.DrsFixture B()
        {
            return _b;
        }

        protected virtual void EnsureOneInstance(com.db4o.inside.replication.TestableReplicationProviderInside
             provider, System.Type clazz)
        {
            EnsureInstanceCount(provider, clazz, 1);
        }

        protected virtual void EnsureInstanceCount(com.db4o.inside.replication.TestableReplicationProviderInside
             provider, System.Type clazz, int count)
        {
            com.db4o.ObjectSet objectSet = provider.GetStoredObjects(clazz);
            Db4oUnit.Assert.AreEqual(count, objectSet.Size());
        }

        protected virtual object GetOneInstance(com.db4o.inside.replication.TestableReplicationProviderInside
             provider, System.Type clazz)
        {
            com.db4o.ObjectSet objectSet = provider.GetStoredObjects(clazz);
            if (1 != objectSet.Size())
            {
                throw new j4o.lang.RuntimeException("Found more than one instance of + " + clazz
                    + " in provider = " + provider);
            }
            return objectSet.Next();
        }

        protected virtual void ReplicateAll(com.db4o.inside.replication.TestableReplicationProviderInside
             providerFrom, com.db4o.inside.replication.TestableReplicationProviderInside providerTo
            )
        {
            com.db4o.replication.ReplicationSession replication = com.db4o.replication.Replication
                .Begin(providerFrom, providerTo);
            com.db4o.ObjectSet allObjects = providerFrom.ObjectsChangedSinceLastReplication();
            if (!allObjects.HasNext())
            {
                throw new j4o.lang.RuntimeException("Can't find any objects to replicate");
            }
            while (allObjects.HasNext())
            {
                object changed = allObjects.Next();
                replication.Replicate(changed);
            }
            replication.Commit();
        }

        protected virtual void ReplicateAll(com.db4o.inside.replication.TestableReplicationProviderInside
             from, com.db4o.inside.replication.TestableReplicationProviderInside to, com.db4o.replication.ReplicationEventListener
             listener)
        {
            com.db4o.replication.ReplicationSession replication = com.db4o.replication.Replication
                .Begin(from, to, listener);
            com.db4o.ObjectSet allObjects = from.ObjectsChangedSinceLastReplication();
            while (allObjects.HasNext())
            {
                object changed = allObjects.Next();
                replication.Replicate(changed);
            }
            replication.Commit();
        }

        protected virtual void Delete(System.Type[] classes)
        {
            _a.Clean();
        }

        protected virtual void ReplicateClass(com.db4o.inside.replication.TestableReplicationProviderInside
             providerA, com.db4o.inside.replication.TestableReplicationProviderInside providerB
            , System.Type clazz)
        {
            com.db4o.replication.ReplicationSession replication = com.db4o.replication.Replication
                .Begin(providerA, providerB);
            com.db4o.ObjectSet allObjects = providerA.ObjectsChangedSinceLastReplication(clazz
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
                j4o.lang.Thread.Sleep(millis);
            }
            catch (System.Exception e)
            {
                throw new j4o.lang.RuntimeException(e);
            }
        }
    }
}
