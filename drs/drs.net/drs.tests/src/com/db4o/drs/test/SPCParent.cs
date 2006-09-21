namespace com.db4o.drs.test
{
	public class SPCParent
	{
		private com.db4o.drs.test.SPCChild child;

		private string name;

		public SPCParent()
		{
		}

		public SPCParent(string name)
		{
			this.name = name;
		}

		public SPCParent(com.db4o.drs.test.SPCChild child, string name)
		{
			this.child = child;
			this.name = name;
		}

		public virtual com.db4o.drs.test.SPCChild GetChild()
		{
			return child;
		}

		public virtual void SetChild(com.db4o.drs.test.SPCChild child)
		{
			this.child = child;
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
			return "SPCParent{" + "child=" + child + ", name='" + name + '\'' + '}';
		}
	}
}
