namespace Db4objects.Db4o.Drs.Test
{
    public abstract class DrsTestCase : Db4oUnit.TestCase, Db4oUnit.TestLifeCycle
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

        private Db4objects.Db4o.Drs.Test.DrsFixture _a;

        private Db4objects.Db4o.Drs.Test.DrsFixture _b;

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

        public virtual void A(Db4objects.Db4o.Drs.Test.DrsFixture fixture)
        {
            _a = fixture;
        }

        public virtual void B(Db4objects.Db4o.Drs.Test.DrsFixture fixture)
        {
            _b = fixture;
        }

        public virtual Db4objects.Db4o.Drs.Test.DrsFixture A()
        {
            return _a;
        }

        public virtual Db4objects.Db4o.Drs.Test.DrsFixture B()
        {
            return _b;
        }

        protected virtual void EnsureOneInstance(Db4objects.Db4o.Drsinside.TestableReplicationProviderInside
             provider, System.Type clazz)
        {
            EnsureInstanceCount(provider, clazz, 1);
        }

        protected virtual void EnsureInstanceCount(Db4objects.Db4o.Drsinside.TestableReplicationProviderInside
             provider, System.Type clazz, int count)
        {
            Db4objects.Db4o.ObjectSet objectSet = provider.GetStoredObjects(clazz);
            Db4oUnit.Assert.AreEqual(count, objectSet.Size());
        }

        protected virtual object GetOneInstance(Db4objects.Db4o.Drsinside.TestableReplicationProviderInside
             provider, System.Type clazz)
        {
            Db4objects.Db4o.ObjectSet objectSet = provider.GetStoredObjects(clazz);
            if (1 != objectSet.Size())
            {
                throw new j4o.lang.RuntimeException("Found more than one instance of + " + clazz
                    + " in provider = " + provider);
            }
            return objectSet.Next();
        }

        protected virtual void ReplicateAll(Db4objects.Db4o.Drsinside.TestableReplicationProviderInside
             providerFrom, Db4objects.Db4o.Drsinside.TestableReplicationProviderInside providerTo)
        {
            Db4objects.Db4o.DrsReplicationSession replication = Db4objects.Db4o.DrsReplication.Begin(providerFrom
                , providerTo);
            Db4objects.Db4o.ObjectSet allObjects = providerFrom.ObjectsChangedSinceLastReplication();
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

        protected virtual void ReplicateAll(Db4objects.Db4o.Drsinside.TestableReplicationProviderInside
             from, Db4objects.Db4o.Drsinside.TestableReplicationProviderInside to, Db4objects.Db4o.DrsReplicationEventListener
             listener)
        {
            Db4objects.Db4o.DrsReplicationSession replication = Db4objects.Db4o.DrsReplication.Begin(from
                , to, listener);
            Db4objects.Db4o.ObjectSet allObjects = from.ObjectsChangedSinceLastReplication();
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

        protected virtual void ReplicateClass(Db4objects.Db4o.Drsinside.TestableReplicationProviderInside
             providerA, Db4objects.Db4o.Drsinside.TestableReplicationProviderInside providerB, System.Type
             clazz)
        {
            Db4objects.Db4o.DrsReplicationSession replication = Db4objects.Db4o.DrsReplication.Begin(providerA
                , providerB);
            Db4objects.Db4o.ObjectSet allObjects = providerA.ObjectsChangedSinceLastReplication(clazz
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
