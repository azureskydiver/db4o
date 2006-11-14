namespace Db4objects.Drs.Test
{
    /// <exclude></exclude>
    public abstract class DrsTestSuite : Db4objects.Drs.Test.DrsTestCase, Db4oUnit.ITestSuiteBuilder
    {
        public virtual Db4oUnit.TestSuite Build()
        {
            return new Db4objects.Drs.Test.DrsTestSuiteBuilder(A(), B(), TestCases()).Build();
        }

        protected virtual System.Type[] TestCases()
        {
            return Shared();
        }

        protected virtual System.Type[] Shared()
        {
            return new System.Type[] { typeof(TheSimplest),
			                           typeof(ReplicationEventTest), 
                                       typeof(ReplicationProviderTest),
                                       typeof(ReplicationAfterDeletionTest),
                                       typeof(SimpleArrayTest), 
			                           typeof(SimpleParentChild),
                                       typeof(ListTest),
                                       typeof(Db4oListTest),
                                       typeof(SingleTypeCollectionReplicationTest),
                                       typeof(R0to4Runner),
                                       typeof(ReplicationFeaturesMain),
                                       typeof(CollectionHandlerImplTest),
                                       typeof(ReplicationTraversalTest)
            };
        }
    }
}
