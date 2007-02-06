namespace com.db4o.@internal.query.processor
{
	/// <summary>Join constraint on queries</summary>
	/// <exclude></exclude>
	public class QConJoin : com.db4o.@internal.query.processor.QCon
	{
		public bool i_and;

		public com.db4o.@internal.query.processor.QCon i_constraint1;

		public com.db4o.@internal.query.processor.QCon i_constraint2;

		public QConJoin()
		{
		}

		internal QConJoin(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.query.processor.QCon
			 a_c1, com.db4o.@internal.query.processor.QCon a_c2, bool a_and) : base(a_trans)
		{
			i_constraint1 = a_c1;
			i_constraint2 = a_c2;
			i_and = a_and;
		}

		internal override void DoNotInclude(com.db4o.@internal.query.processor.QCandidate
			 a_root)
		{
			i_constraint1.DoNotInclude(a_root);
			i_constraint2.DoNotInclude(a_root);
		}

		internal override void ExchangeConstraint(com.db4o.@internal.query.processor.QCon
			 a_exchange, com.db4o.@internal.query.processor.QCon a_with)
		{
			base.ExchangeConstraint(a_exchange, a_with);
			if (a_exchange == i_constraint1)
			{
				i_constraint1 = a_with;
			}
			if (a_exchange == i_constraint2)
			{
				i_constraint2 = a_with;
			}
		}

		internal virtual void EvaluatePending(com.db4o.@internal.query.processor.QCandidate
			 a_root, com.db4o.@internal.query.processor.QPending a_pending, int a_secondResult
			)
		{
			bool res = i_evaluator.Not(i_and ? ((a_pending._result + a_secondResult) > 0) : (
				a_pending._result + a_secondResult) > -4);
			if (HasJoins())
			{
				System.Collections.IEnumerator i = IterateJoins();
				while (i.MoveNext())
				{
					com.db4o.@internal.query.processor.QConJoin qcj = (com.db4o.@internal.query.processor.QConJoin
						)i.Current;
					a_root.Evaluate(new com.db4o.@internal.query.processor.QPending(qcj, this, res));
				}
			}
			else
			{
				if (!res)
				{
					i_constraint1.DoNotInclude(a_root);
					i_constraint2.DoNotInclude(a_root);
				}
			}
		}

		public virtual com.db4o.@internal.query.processor.QCon GetOtherConstraint(com.db4o.@internal.query.processor.QCon
			 a_constraint)
		{
			if (a_constraint == i_constraint1)
			{
				return i_constraint2;
			}
			else
			{
				if (a_constraint == i_constraint2)
				{
					return i_constraint1;
				}
			}
			throw new System.ArgumentException();
		}

		internal override string LogObject()
		{
			return string.Empty;
		}

		internal virtual bool RemoveForParent(com.db4o.@internal.query.processor.QCon a_constraint
			)
		{
			if (i_and)
			{
				com.db4o.@internal.query.processor.QCon other = GetOtherConstraint(a_constraint);
				other.RemoveJoin(this);
				other.Remove();
				return true;
			}
			return false;
		}

		public override string ToString()
		{
			return base.ToString();
			string str = "QConJoin " + (i_and ? "AND " : "OR");
			if (i_constraint1 != null)
			{
				str += "\n   " + i_constraint1;
			}
			if (i_constraint2 != null)
			{
				str += "\n   " + i_constraint2;
			}
			return str;
		}

		public virtual bool IsOr()
		{
			return !i_and;
		}
	}
}
