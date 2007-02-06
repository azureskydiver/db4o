namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QEStartsWith : com.db4o.@internal.query.processor.QEStringCmp
	{
		public QEStartsWith(bool caseSensitive_) : base(caseSensitive_)
		{
		}

		protected override bool CompareStrings(string candidate, string constraint)
		{
			return candidate.IndexOf(constraint) == 0;
		}
	}
}
