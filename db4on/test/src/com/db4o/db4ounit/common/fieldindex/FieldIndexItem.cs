namespace com.db4o.db4ounit.common.fieldindex
{
	public class FieldIndexItem : com.db4o.db4ounit.common.fieldindex.HasFoo
	{
		public int foo;

		public FieldIndexItem()
		{
		}

		public FieldIndexItem(int foo_)
		{
			foo = foo_;
		}

		public virtual int GetFoo()
		{
			return foo;
		}
	}
}
