namespace com.db4o.db4ounit.common.soda.classes.typedhierarchy
{
	public class STETH4 : com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH2
	{
		public string foo4;

		public STETH4()
		{
		}

		public STETH4(string str1, string str2, string str3) : base(str1, str2)
		{
			foo4 = str3;
		}
	}
}
