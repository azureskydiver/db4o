
namespace com.db4o.config
{
	/// <exclude></exclude>
	public class Entry : com.db4o.config.Compare, com.db4o.types.SecondClass
	{
		public object key;

		public object value;

		public virtual object compare()
		{
			return key;
		}
	}
}
