
namespace com.db4o
{
	/// <exclude></exclude>
	public class QEGreater : com.db4o.QEAbstract
	{
		internal override bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (a_value == null)
			{
				return false;
			}
			return a_constraint.getComparator(a_candidate).isGreater(a_value);
		}

		internal override void indexBitMap(bool[] bits)
		{
			bits[2] = true;
		}
	}
}
