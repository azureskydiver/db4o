namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QConEvaluation : com.db4o.@internal.query.processor.QCon
	{
		[System.NonSerialized]
		private object i_evaluation;

		public byte[] i_marshalledEvaluation;

		public int i_marshalledID;

		public QConEvaluation()
		{
		}

		public QConEvaluation(com.db4o.@internal.Transaction a_trans, object a_evaluation
			) : base(a_trans)
		{
			i_evaluation = a_evaluation;
		}

		internal override void EvaluateEvaluationsExec(com.db4o.@internal.query.processor.QCandidates
			 a_candidates, bool rereadObject)
		{
			if (rereadObject)
			{
				a_candidates.Traverse(new _AnonymousInnerClass32(this));
			}
			a_candidates.Filter(this);
		}

		private sealed class _AnonymousInnerClass32 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass32(QConEvaluation _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				((com.db4o.@internal.query.processor.QCandidate)a_object).UseField(null);
			}

			private readonly QConEvaluation _enclosing;
		}

		internal override void Marshall()
		{
			base.Marshall();
			int[] id = { 0 };
			i_marshalledEvaluation = i_trans.Stream().Marshall(com.db4o.@internal.Platform4.WrapEvaluation
				(i_evaluation), id);
			i_marshalledID = id[0];
		}

		internal override void Unmarshall(com.db4o.@internal.Transaction a_trans)
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
			com.db4o.@internal.query.processor.QCandidate candidate = (com.db4o.@internal.query.processor.QCandidate
				)obj;
			try
			{
				com.db4o.@internal.Platform4.EvaluationEvaluate(i_evaluation, candidate);
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
