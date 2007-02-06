namespace com.db4o.db4ounit.common.foundation
{
	public class AllTests : Db4oUnit.TestSuiteBuilder
	{
		public virtual Db4oUnit.TestSuite Build()
		{
			return new Db4oUnit.ReflectionTestSuiteBuilder(new System.Type[] { typeof(com.db4o.db4ounit.common.foundation.Algorithms4TestCase)
				, typeof(com.db4o.db4ounit.common.foundation.ArrayIterator4TestCase), typeof(com.db4o.db4ounit.common.foundation.Arrays4TestCase)
				, typeof(com.db4o.db4ounit.common.foundation.BitMap4TestCase), typeof(com.db4o.db4ounit.common.foundation.Collection4TestCase)
				, typeof(com.db4o.db4ounit.common.foundation.CompositeIterator4TestCase), typeof(com.db4o.db4ounit.common.foundation.Hashtable4TestCase)
				, typeof(com.db4o.db4ounit.common.foundation.IntArrayListTestCase), typeof(com.db4o.db4ounit.common.foundation.Iterable4AdaptorTestCase)
				, typeof(com.db4o.db4ounit.common.foundation.IteratorsTestCase), typeof(com.db4o.db4ounit.common.foundation.Queue4TestCase)
				, typeof(com.db4o.db4ounit.common.foundation.SortedCollection4TestCase), typeof(com.db4o.db4ounit.common.foundation.Stack4TestCase)
				, typeof(com.db4o.db4ounit.common.foundation.TreeKeyIteratorTestCase), typeof(com.db4o.db4ounit.common.foundation.BufferTestCase)
				 }).Build();
		}

		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(com.db4o.db4ounit.common.foundation.AllTests)).Run
				();
		}
	}
}
