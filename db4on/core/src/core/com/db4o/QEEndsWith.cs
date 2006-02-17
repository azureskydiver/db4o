namespace com.db4o
{
	/// <exclude></exclude>
	public class QEEndsWith : com.db4o.QEStringCmp
	{
		public QEEndsWith(bool caseSensitive) : base(caseSensitive)
		{
		}

		protected override bool compareStrings(string candidate, string constraint)
		{
			return candidate.LastIndexOf(constraint) == j4o.lang.JavaSystem.getLengthOf(candidate
				) - j4o.lang.JavaSystem.getLengthOf(constraint);
		}
	}
}
