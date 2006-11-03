namespace com.db4o
{
	/// <exclude></exclude>
	public class QConEvaluation : com.db4o.QCon
	{
		[com.db4o.Transient]
		private object i_evaluation;

		public byte[] i_marshalledEvaluation;

		public int i_marshalledID;

		public QConEvaluation()
		{
		}

		internal QConEvaluation(com.db4o.Transaction a_trans, object a_evaluation) : base
			(a_trans)
		{
			i_evaluation = a_evaluation;
		}

		internal override void EvaluateEvaluationsExec(com.db4o.QCandidates a_candidates, 
			bool rereadObject)
		{
			if (rereadObject)
			{
				a_candidates.Traverse(new _AnonymousInnerClass29(this));
			}
			a_candidates.Filter(this);
		}

		private sealed class _AnonymousInnerClass29 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass29(QConEvaluation _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				((com.db4o.QCandidate)a_object).UseField(null);
			}

			private readonly QConEvaluation _enclosing;
		}

		internal override void Marshall()
		{
			base.Marshall();
			int[] id = { 0 };
			i_marshalledEvaluation = i_trans.Stream().Marshall(com.db4o.Platform4.WrapEvaluation
				(i_evaluation), id);
			i_marshalledID = id[0];
		}

		internal override void Unmarshall(com.db4o.Transaction a_trans)
		{
			if (i_trans == null)
			{
				base.Unmarshall(a_trans);
				i_evaluation = i_trans.Stream().Unmarshall(i_marshalledEvaluation, i_marshalledID
					);
			}
		}

		public override void Visit(object obj)
		{
			com.db4o.QCandidate candidate = (com.db4o.QCandidate)obj;
			try
			{
				com.db4o.Platform4.EvaluationEvaluate(i_evaluation, candidate);
				if (!candidate._include)
				{
					DoNotInclude(candidate.GetRoot());
				}
			}
			catch
			{
				candidate.Include(false);
				DoNotInclude(candidate.GetRoot());
			}
		}

		internal virtual bool SupportsIndex()
		{
			return false;
		}
	}
}
