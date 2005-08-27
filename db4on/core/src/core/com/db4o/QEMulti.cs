
namespace com.db4o
{
	/// <exclude></exclude>
	public class QEMulti : com.db4o.QE
	{
		public com.db4o.foundation.Collection4 i_evaluators = new com.db4o.foundation.Collection4
			();

		internal override com.db4o.QE add(com.db4o.QE evaluator)
		{
			i_evaluators.ensure(evaluator);
			return this;
		}

		internal override bool identity()
		{
			bool ret = false;
			com.db4o.foundation.Iterator4 i = i_evaluators.iterator();
			while (i.hasNext())
			{
				if (((com.db4o.QE)i.next()).identity())
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

		internal override bool isDefault()
		{
			return false;
		}

		internal override bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			com.db4o.foundation.Iterator4 i = i_evaluators.iterator();
			while (i.hasNext())
			{
				if (((com.db4o.QE)i.next()).evaluate(a_constraint, a_candidate, a_value))
				{
					return true;
				}
			}
			return false;
		}

		internal override void indexBitMap(bool[] bits)
		{
			com.db4o.foundation.Iterator4 i = i_evaluators.iterator();
			while (i.hasNext())
			{
				((com.db4o.QE)i.next()).indexBitMap(bits);
			}
		}

		internal override bool supportsIndex()
		{
			com.db4o.foundation.Iterator4 i = i_evaluators.iterator();
			while (i.hasNext())
			{
				if (!((com.db4o.QE)i.next()).supportsIndex())
				{
					return false;
				}
			}
			return true;
		}
	}
}
