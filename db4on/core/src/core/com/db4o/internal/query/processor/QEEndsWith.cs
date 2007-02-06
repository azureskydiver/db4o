namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QEEndsWith : com.db4o.@internal.query.processor.QEStringCmp
	{
		public QEEndsWith(bool caseSensitive_) : base(caseSensitive_)
		{
		}

		protected override bool CompareStrings(string candidate, string constraint)
		{
			return candidate.LastIndexOf(constraint) == candidate.Length - constraint.Length;
		}
	}
}
