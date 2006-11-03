namespace com.db4o.db4ounit.common.soda.classes.typedhierarchy
{
	public class STETH2 : com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH1TestCase
	{
		public string foo2;

		public STETH2()
		{
		}

		public STETH2(string str1, string str2) : base(str1)
		{
			foo2 = str2;
		}
	}
}
