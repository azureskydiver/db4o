namespace com.db4o.db4ounit.common.fieldindex
{
	public class ComplexFieldIndexItem : com.db4o.db4ounit.common.fieldindex.HasFoo
	{
		public int foo;

		public int bar;

		public com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem child;

		public ComplexFieldIndexItem()
		{
		}

		public ComplexFieldIndexItem(int foo_, int bar_, com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem
			 child_)
		{
			foo = foo_;
			bar = bar_;
			child = child_;
		}

		public virtual int GetFoo()
		{
			return foo;
		}
	}
}
