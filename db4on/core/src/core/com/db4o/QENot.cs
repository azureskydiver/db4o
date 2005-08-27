
namespace com.db4o
{
	/// <exclude></exclude>
	public class QENot : com.db4o.QE
	{
		public com.db4o.QE i_evaluator;

		public QENot()
		{
		}

		internal QENot(com.db4o.QE a_evaluator)
		{
			i_evaluator = a_evaluator;
		}

		internal override com.db4o.QE add(com.db4o.QE evaluator)
		{
			if (!(evaluator is com.db4o.QENot))
			{
				i_evaluator = i_evaluator.add(evaluator);
			}
			return this;
		}

		internal override bool identity()
		{
			return i_evaluator.identity();
		}

		internal override bool isDefault()
		{
			return false;
		}

		internal override bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			return !i_evaluator.evaluate(a_constraint, a_candidate, a_value);
		}

		internal override bool not(bool res)
		{
			return !res;
		}

		internal override void indexBitMap(bool[] bits)
		{
			i_evaluator.indexBitMap(bits);
			for (int i = 0; i < 4; i++)
			{
				bits[i] = !bits[i];
			}
		}

		internal override bool supportsIndex()
		{
			return i_evaluator.supportsIndex();
		}
	}
}
