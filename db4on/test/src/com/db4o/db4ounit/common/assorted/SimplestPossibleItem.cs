namespace com.db4o.db4ounit.common.assorted
{
	public class SimplestPossibleItem
	{
		public string name;

		public SimplestPossibleItem()
		{
		}

		public SimplestPossibleItem(string name_)
		{
			this.name = name_;
		}

		public virtual string GetName()
		{
			return name;
		}
	}
}
