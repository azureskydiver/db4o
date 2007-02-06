namespace com.db4o.@internal.query
{
	/// <exclude></exclude>
	[System.Serializable]
	public class PredicateEvaluation : com.db4o.query.Evaluation
	{
		public com.db4o.query.Predicate _predicate;

		public PredicateEvaluation()
		{
		}

		public PredicateEvaluation(com.db4o.query.Predicate predicate)
		{
			_predicate = predicate;
		}

		public virtual void Evaluate(com.db4o.query.Candidate candidate)
		{
			candidate.Include(_predicate.AppliesTo(candidate.GetObject()));
		}
	}
}
