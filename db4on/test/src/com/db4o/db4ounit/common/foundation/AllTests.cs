namespace com.db4o.db4ounit.common.foundation
{
	public class AllTests : Db4oUnit.TestSuiteBuilder
	{
		public virtual Db4oUnit.TestSuite Build()
		{
			return new Db4oUnit.ReflectionTestSuiteBuilder(new System.Type[] { typeof(com.db4o.db4ounit.common.foundation.ArrayIterator4TestCase
				), typeof(com.db4o.db4ounit.common.foundation.SortedCollection4TestCase), typeof(
				com.db4o.db4ounit.common.foundation.BitMap4TestCase), typeof(com.db4o.db4ounit.common.foundation.Collection4TestCase
				), typeof(com.db4o.db4ounit.common.foundation.CompositeIterator4TestCase), typeof(
				com.db4o.db4ounit.common.foundation.IteratorsTestCase), typeof(com.db4o.db4ounit.common.foundation.Hashtable4TestCase
				), typeof(com.db4o.db4ounit.common.foundation.YapReaderTestCase) }).Build();
		}

		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(com.db4o.db4ounit.common.foundation.AllTests)).Run
				();
		}
	}
}
