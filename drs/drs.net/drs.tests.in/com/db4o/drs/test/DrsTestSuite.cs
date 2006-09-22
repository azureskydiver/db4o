namespace com.db4o.drs.test
{
    /// <exclude></exclude>
    public abstract class DrsTestSuite : com.db4o.drs.test.DrsTestCase, Db4oUnit.TestSuiteBuilder
    {
        public virtual Db4oUnit.TestSuite Build()
        {
            return new com.db4o.drs.test.DrsTestSuiteBuilder(A(), B(), TestCases()).Build();
        }

        protected virtual System.Type[] TestCases()
        {
            return All();
        }

        protected virtual System.Type[] One()
        {
            return new System.Type[] { typeof(TheSimplest) };
        }

        protected virtual System.Type[] All()
        {
            return new System.Type[] { typeof(com.db4o.drs.test.TheSimplest), 
			                           typeof(ReplicationEventTest), 
			                           typeof(SimpleArrayTest), 
			                           typeof(SimpleParentChild),
                                        typeof(ReplicationAfterDeletionTest),
                                        typeof(ListTest),
                                        typeof(Db4oListTest),
                                        typeof(R0to4Runner)};
        }
    }
}
