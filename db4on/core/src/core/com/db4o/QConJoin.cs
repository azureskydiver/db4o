namespace com.db4o
{
	/// <summary>Join constraint on queries</summary>
	/// <exclude></exclude>
	public class QConJoin : com.db4o.QCon
	{
		public bool i_and;

		public com.db4o.QCon i_constraint1;

		public com.db4o.QCon i_constraint2;

		public QConJoin()
		{
		}

		internal QConJoin(com.db4o.Transaction a_trans, com.db4o.QCon a_c1, com.db4o.QCon
			 a_c2, bool a_and) : base(a_trans)
		{
			i_constraint1 = a_c1;
			i_constraint2 = a_c2;
			i_and = a_and;
		}

		internal override void doNotInclude(com.db4o.QCandidate a_root)
		{
			i_constraint1.doNotInclude(a_root);
			i_constraint2.doNotInclude(a_root);
		}

		internal override void exchangeConstraint(com.db4o.QCon a_exchange, com.db4o.QCon
			 a_with)
		{
			base.exchangeConstraint(a_exchange, a_with);
			if (a_exchange == i_constraint1)
			{
				i_constraint1 = a_with;
			}
			if (a_exchange == i_constraint2)
			{
				i_constraint2 = a_with;
			}
		}

		internal virtual void evaluatePending(com.db4o.QCandidate a_root, com.db4o.QPending
			 a_pending, com.db4o.QPending a_secondPending, int a_secondResult)
		{
			bool res = i_evaluator.not(i_and ? ((a_pending.i_result + a_secondResult) > 0) : 
				(a_pending.i_result + a_secondResult) > -4);
			if (hasJoins())
			{
				com.db4o.foundation.Iterator4 i = iterateJoins();
				while (i.hasNext())
				{
					com.db4o.QConJoin qcj = (com.db4o.QConJoin)i.next();
					a_root.evaluate(new com.db4o.QPending(qcj, this, res));
				}
			}
			else
			{
				if (!res)
				{
					i_constraint1.doNotInclude(a_root);
					i_constraint2.doNotInclude(a_root);
				}
			}
		}

		internal virtual com.db4o.QCon getOtherConstraint(com.db4o.QCon a_constraint)
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
			return null;
		}

		internal override string logObject()
		{
			return "";
		}

		internal virtual bool removeForParent(com.db4o.QCon a_constraint)
		{
			if (i_and)
			{
				com.db4o.QCon other = getOtherConstraint(a_constraint);
				other.removeJoin(this);
				other.remove();
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
	}
}
