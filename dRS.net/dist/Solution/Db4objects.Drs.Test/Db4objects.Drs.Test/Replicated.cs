namespace Db4objects.Drs.Test
{
	public class Replicated
	{
		private string name;

		private Db4objects.Drs.Test.Replicated link;

		public Replicated()
		{
		}

		public Replicated(string name)
		{
			this.SetName(name);
		}

		public override string ToString()
		{
			return GetName() + ", hashcode = " + GetHashCode() + ", identity = " + Sharpen.Runtime
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

		public virtual Db4objects.Drs.Test.Replicated GetLink()
		{
			return link;
		}

		public virtual void SetLink(Db4objects.Drs.Test.Replicated link)
		{
			this.link = link;
		}

		public override bool Equals(object o)
		{
			if (o == null)
			{
				return false;
			}
			if (!(o is Db4objects.Drs.Test.Replicated))
			{
				return false;
			}
			return ((Db4objects.Drs.Test.Replicated)o).name.Equals(name);
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
