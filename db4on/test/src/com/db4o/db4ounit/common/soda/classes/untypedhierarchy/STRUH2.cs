namespace com.db4o.db4ounit.common.soda.classes.untypedhierarchy
{
	public class STRUH2
	{
		public object parent;

		public object h3;

		public string foo2;

		public STRUH2()
		{
		}

		public STRUH2(com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3 a3)
		{
			h3 = a3;
			a3.parent = this;
		}

		public STRUH2(string str)
		{
			foo2 = str;
		}

		public STRUH2(com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3 a3, string
			 str)
		{
			h3 = a3;
			a3.parent = this;
			foo2 = str;
		}
	}
}
