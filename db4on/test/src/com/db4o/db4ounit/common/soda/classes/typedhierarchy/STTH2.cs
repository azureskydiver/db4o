namespace com.db4o.db4ounit.common.soda.classes.typedhierarchy
{
	public class STTH2
	{
		public com.db4o.db4ounit.common.soda.classes.typedhierarchy.STTH3 h3;

		public string foo2;

		public STTH2()
		{
		}

		public STTH2(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STTH3 a3)
		{
			h3 = a3;
		}

		public STTH2(string str)
		{
			foo2 = str;
		}

		public STTH2(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STTH3 a3, string
			 str)
		{
			h3 = a3;
			foo2 = str;
		}
	}
}
