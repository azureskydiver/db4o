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

		internal override void evaluateEvaluationsExec(com.db4o.QCandidates a_candidates, 
			bool rereadObject)
		{
			if (rereadObject)
			{
				a_candidates.traverse(new _AnonymousInnerClass29(this));
			}
			a_candidates.filter(this);
		}

		private sealed class _AnonymousInnerClass29 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass29(QConEvaluation _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				((com.db4o.QCandidate)a_object).useField(null);
			}

			private readonly QConEvaluation _enclosing;
		}

		internal override void marshall()
		{
			base.marshall();
			int[] id = { 0 };
			i_marshalledEvaluation = i_trans.i_stream.marshall(com.db4o.Platform4.wrapEvaluation
				(i_evaluation), id);
			i_marshalledID = id[0];
		}

		internal override void unmarshall(com.db4o.Transaction a_trans)
		{
			if (i_trans == null)
			{
				base.unmarshall(a_trans);
				i_evaluation = i_trans.i_stream.unmarshall(i_marshalledEvaluation, i_marshalledID
					);
			}
		}

		public override void visit(object obj)
		{
			com.db4o.QCandidate candidate = (com.db4o.QCandidate)obj;
			try
			{
				com.db4o.Platform4.evaluationEvaluate(i_evaluation, candidate);
				if (!candidate.i_include)
				{
					doNotInclude(candidate.getRoot());
				}
			}
			catch (System.Exception e)
			{
				candidate.include(false);
				doNotInclude(candidate.getRoot());
			}
		}

		internal virtual bool supportsIndex()
		{
			return false;
		}
	}
}
