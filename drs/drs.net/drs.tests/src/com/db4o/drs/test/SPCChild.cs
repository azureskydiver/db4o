namespace com.db4o.drs.test
{
	public class SPCChild
	{
		private string name;

		public SPCChild()
		{
		}

		public SPCChild(string name)
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
			return "#############################3SPCChild{" + "name='" + name + '\'' + '}';
		}
	}
}
