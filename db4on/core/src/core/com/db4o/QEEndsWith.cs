namespace com.db4o
{
	/// <exclude></exclude>
	public class QEEndsWith : com.db4o.QEStringCmp
	{
		public QEEndsWith(bool caseSensitive) : base(caseSensitive)
		{
		}

		protected override bool CompareStrings(string candidate, string constraint)
		{
			return candidate.LastIndexOf(constraint) == candidate.Length - constraint.Length;
		}
	}
}
