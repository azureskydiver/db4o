namespace com.db4o
{
	/// <exclude></exclude>
	public class QEContains : com.db4o.QEStringCmp
	{
		public QEContains(bool caseSensitive) : base(caseSensitive)
		{
		}

		protected override bool compareStrings(string candidate, string constraint)
		{
			return candidate.IndexOf(constraint) > -1;
		}
	}
}
