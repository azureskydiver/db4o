namespace Db4objects.Drs.Test
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
