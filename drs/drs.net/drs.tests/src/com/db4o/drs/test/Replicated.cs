namespace com.db4o.drs.test
{
	public class Replicated
	{
		private string name;

		private com.db4o.drs.test.Replicated link;

		public Replicated()
		{
		}

		public Replicated(string name)
		{
			this.SetName(name);
		}

		public override string ToString()
		{
			return GetName() + ", hashcode = " + GetHashCode() + ", identity = " + j4o.lang.JavaSystem
				.IdentityHashCode(this);
		}

		public virtual string GetName()
		{
			return name;
		}

		public virtual void SetName(string name)
		{
			this.name = name;
		}

		public virtual com.db4o.drs.test.Replicated GetLink()
		{
			return link;
		}

		public virtual void SetLink(com.db4o.drs.test.Replicated link)
		{
			this.link = link;
		}

		public override bool Equals(object o)
		{
			if (o == null)
			{
				return false;
			}
			if (!(o is com.db4o.drs.test.Replicated))
			{
				return false;
			}
			return ((com.db4o.drs.test.Replicated)o).name.Equals(name);
		}

		public override int GetHashCode()
		{
			if (name == null)
			{
				return 0;
			}
			return name.GetHashCode();
		}
	}
}
