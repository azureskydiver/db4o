namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public abstract class ObjectHeaderAttributes
	{
		public abstract void AddBaseLength(int length);

		public abstract void AddPayLoadLength(int length);

		public abstract void PrepareIndexedPayLoadEntry(com.db4o.Transaction trans);
	}
}
