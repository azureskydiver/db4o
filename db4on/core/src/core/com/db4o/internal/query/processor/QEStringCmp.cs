namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public abstract class QEStringCmp : com.db4o.@internal.query.processor.QEAbstract
	{
		public bool caseSensitive;

		public QEStringCmp(bool caseSensitive_)
		{
			caseSensitive = caseSensitive_;
		}

		internal override bool Evaluate(com.db4o.@internal.query.processor.QConObject a_constraint
			, com.db4o.@internal.query.processor.QCandidate a_candidate, object a_value)
		{
			if (a_value != null)
			{
				if (a_value is com.db4o.@internal.Buffer)
				{
					a_value = a_candidate._marshallerFamily._string.ReadFromOwnSlot(a_constraint.i_trans
						.Stream(), ((com.db4o.@internal.Buffer)a_value));
				}
				string candidate = a_value.ToString();
				string constraint = a_constraint.i_object.ToString();
				if (!caseSensitive)
				{
					candidate = candidate.ToLower();
					constraint = constraint.ToLower();
				}
				return CompareStrings(candidate, constraint);
			}
			return a_constraint.i_object == null;
		}

		public override bool SupportsIndex()
		{
			return false;
		}

		protected abstract bool CompareStrings(string candidate, string constraint);
	}
}
