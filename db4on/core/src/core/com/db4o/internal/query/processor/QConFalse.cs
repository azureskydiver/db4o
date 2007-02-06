namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QConFalse : com.db4o.@internal.query.processor.QConPath
	{
		public QConFalse()
		{
		}

		internal QConFalse(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.query.processor.QCon
			 a_parent, com.db4o.@internal.query.processor.QField a_field) : base(a_trans, a_parent
			, a_field)
		{
		}

		internal override void CreateCandidates(com.db4o.foundation.Collection4 a_candidateCollection
			)
		{
		}

		internal override bool Evaluate(com.db4o.@internal.query.processor.QCandidate a_candidate
			)
		{
			return false;
		}
	}
}
