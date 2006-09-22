namespace com.db4o.db4ounit.common.fieldindex
{
	/// <exclude></exclude>
	public class NonIndexedFieldIndexItem : com.db4o.db4ounit.common.fieldindex.HasFoo
	{
		public int foo;

		public int indexed;

		public NonIndexedFieldIndexItem()
		{
		}

		public NonIndexedFieldIndexItem(int foo_)
		{
			foo = foo_;
			indexed = foo_;
		}

		public virtual int GetFoo()
		{
			return foo;
		}
	}
}
