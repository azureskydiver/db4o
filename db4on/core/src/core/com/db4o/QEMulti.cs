namespace com.db4o
{
	/// <exclude></exclude>
	public class QEMulti : com.db4o.QE
	{
		public com.db4o.foundation.Collection4 i_evaluators = new com.db4o.foundation.Collection4
			();

		internal override com.db4o.QE Add(com.db4o.QE evaluator)
		{
			i_evaluators.Ensure(evaluator);
			return this;
		}

		internal override bool Identity()
		{
			bool ret = false;
			com.db4o.foundation.Iterator4 i = i_evaluators.Iterator();
			while (i.HasNext())
			{
				if (((com.db4o.QE)i.Next()).Identity())
				{
					ret = true;
				}
				else
				{
					return false;
				}
			}
			return ret;
		}

		internal override bool IsDefault()
		{
			return false;
		}

		internal override bool Evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			com.db4o.foundation.Iterator4 i = i_evaluators.Iterator();
			while (i.HasNext())
			{
				if (((com.db4o.QE)i.Next()).Evaluate(a_constraint, a_candidate, a_value))
				{
					return true;
				}
			}
			return false;
		}

		public override void IndexBitMap(bool[] bits)
		{
			com.db4o.foundation.Iterator4 i = i_evaluators.Iterator();
			while (i.HasNext())
			{
				((com.db4o.QE)i.Next()).IndexBitMap(bits);
			}
		}

		public override bool SupportsIndex()
		{
			com.db4o.foundation.Iterator4 i = i_evaluators.Iterator();
			while (i.HasNext())
			{
				if (!((com.db4o.QE)i.Next()).SupportsIndex())
				{
					return false;
				}
			}
			return true;
		}
	}
}
