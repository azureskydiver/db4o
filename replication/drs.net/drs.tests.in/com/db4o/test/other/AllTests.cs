namespace com.db4o.test.other
{
    public class AllTests : com.db4o.test.replication.db4ounit.DrsTestSuite
    {
        protected override System.Type[] TestCases()
        {
            return new System.Type[] { typeof(TheSimplest), 
                                       typeof(GetByUUID),
                                       typeof(SimpleParentChild),
                                       typeof(ReplicationEventTest) };
        }

        public static void Main(string[] args)
        {
            new com.db4o.test.other.AllTests().RunDb4oDb4o();
            new com.db4o.test.other.AllTests().Rundb4oCS();
            new com.db4o.test.other.AllTests().RunCSdb4o();
            new com.db4o.test.other.AllTests().RunCSCS();
        }
    }
}
