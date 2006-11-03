namespace com.db4o.db4ounit.common.sampledata
{
	public class AtomData
	{
		public com.db4o.db4ounit.common.sampledata.AtomData child;

		public string name;

		public AtomData()
		{
		}

		public AtomData(com.db4o.db4ounit.common.sampledata.AtomData child)
		{
			this.child = child;
		}

		public AtomData(string name)
		{
			this.name = name;
		}

		public AtomData(com.db4o.db4ounit.common.sampledata.AtomData child, string name) : 
			this(child)
		{
			this.name = name;
		}

		public override int GetHashCode()
		{
			return this.name != null ? this.name.GetHashCode() : 0;
		}

		public override bool Equals(object obj)
		{
			if (obj is com.db4o.db4ounit.common.sampledata.AtomData)
			{
				com.db4o.db4ounit.common.sampledata.AtomData other = (com.db4o.db4ounit.common.sampledata.AtomData
					)obj;
				if (name == null)
				{
					if (other.name != null)
					{
						return false;
					}
				}
				else
				{
					if (!name.Equals(other.name))
					{
						return false;
					}
				}
				if (child != null)
				{
					return child.Equals(other.child);
				}
				return other.child == null;
			}
			return false;
		}

		public override string ToString()
		{
			string str = "Atom(" + name + ")";
			if (child != null)
			{
				return str + "." + child.ToString();
			}
			return str;
		}
	}
}
