namespace com.db4o
{
	/// <exclude></exclude>
	public class QConFalse : com.db4o.QConPath
	{
		public QConFalse()
		{
		}

		internal QConFalse(com.db4o.Transaction a_trans, com.db4o.QCon a_parent, com.db4o.QField
			 a_field) : base(a_trans, a_parent, a_field)
		{
		}

		internal override void createCandidates(com.db4o.foundation.Collection4 a_candidateCollection
			)
		{
		}

		internal override bool evaluate(com.db4o.QCandidate a_candidate)
		{
			return false;
		}
	}
}
