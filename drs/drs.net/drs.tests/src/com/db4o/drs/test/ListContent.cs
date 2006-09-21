namespace com.db4o.drs.test
{
	public class ListContent
	{
		private string name;

		public ListContent()
		{
		}

		public ListContent(string name)
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

		public override string ToString()
		{
			return "name = " + name;
		}
	}
}
