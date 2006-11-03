namespace com.db4o.db4ounit.common.sampledata
{
	public class MoleculeData : com.db4o.db4ounit.common.sampledata.AtomData
	{
		public MoleculeData()
		{
		}

		public MoleculeData(com.db4o.db4ounit.common.sampledata.AtomData child) : base(child
			)
		{
		}

		public MoleculeData(string name) : base(name)
		{
		}

		public MoleculeData(com.db4o.db4ounit.common.sampledata.AtomData child, string name
			) : base(child, name)
		{
		}

		public override bool Equals(object obj)
		{
			if (obj is com.db4o.db4ounit.common.sampledata.MoleculeData)
			{
				return base.Equals(obj);
			}
			return false;
		}

		public override string ToString()
		{
			string str = "Molecule(" + name + ")";
			if (child != null)
			{
				return str + "." + child.ToString();
			}
			return str;
		}
	}
}
