namespace com.db4o.db4ounit.common.soda.classes.typedhierarchy
{
	public class STETH3 : com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH2
	{
		public string foo3;

		public STETH3()
		{
		}

		public STETH3(string str1, string str2, string str3) : base(str1, str2)
		{
			foo3 = str3;
		}
	}
}
