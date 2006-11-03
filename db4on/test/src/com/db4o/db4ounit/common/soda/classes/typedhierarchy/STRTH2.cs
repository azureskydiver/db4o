namespace com.db4o.db4ounit.common.soda.classes.typedhierarchy
{
	public class STRTH2
	{
		public com.db4o.db4ounit.common.soda.classes.typedhierarchy.STRTH1TestCase parent;

		public com.db4o.db4ounit.common.soda.classes.typedhierarchy.STRTH3 h3;

		public string foo2;

		public STRTH2()
		{
		}

		public STRTH2(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STRTH3 a3)
		{
			h3 = a3;
			a3.parent = this;
		}

		public STRTH2(string str)
		{
			foo2 = str;
		}

		public STRTH2(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STRTH3 a3, string
			 str)
		{
			h3 = a3;
			a3.parent = this;
			foo2 = str;
		}
	}
}
