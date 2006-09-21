namespace com.db4o.drs.test
{
	public class SimpleArrayContent
	{
		private string name;

		public SimpleArrayContent()
		{
		}

		public SimpleArrayContent(string name)
		{
			this.name = name;
		}

		public virtual string GetName()
		{
			return name;
		}

		public virtual void SetName(string name)
		{
			this.name = name;
		}
	}
}
