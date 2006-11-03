namespace com.db4o
{
	/// <exclude></exclude>
	public class QEContains : com.db4o.QEStringCmp
	{
		public QEContains(bool caseSensitive_) : base(caseSensitive_)
		{
		}

		protected override bool CompareStrings(string candidate, string constraint)
		{
			return candidate.IndexOf(constraint) > -1;
		}
	}
}
