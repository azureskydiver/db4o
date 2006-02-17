namespace com.db4o
{
	/// <exclude></exclude>
	public class QEStartsWith : com.db4o.QEStringCmp
	{
		public QEStartsWith(bool caseSensitive) : base(caseSensitive)
		{
		}

		protected override bool compareStrings(string candidate, string constraint)
		{
			return candidate.IndexOf(constraint) == 0;
		}
	}
}
