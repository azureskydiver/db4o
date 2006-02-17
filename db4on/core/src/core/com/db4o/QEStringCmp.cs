namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class QEStringCmp : com.db4o.QEAbstract
	{
		private bool caseSensitive;

		public QEStringCmp(bool caseSensitive)
		{
			this.caseSensitive = caseSensitive;
		}

		internal override bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (a_value != null)
			{
				if (a_value is com.db4o.YapReader)
				{
					a_value = ((com.db4o.YapReader)a_value).toString(a_constraint.i_trans);
				}
				string candidate = a_value.ToString();
				string constraint = a_constraint.i_object.ToString();
				if (!caseSensitive)
				{
					candidate = candidate.ToLower();
					constraint = constraint.ToLower();
				}
				return compareStrings(candidate, constraint);
			}
			return a_constraint.i_object.Equals(null);
		}

		public override bool supportsIndex()
		{
			return false;
		}

		protected abstract bool compareStrings(string candidate, string constraint);
	}
}
