namespace com.db4o
{
	/// <exclude></exclude>
	public class QEIdentity : com.db4o.QEEqual
	{
		public int i_objectID;

		internal override bool identity()
		{
			return true;
		}

		internal override bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (i_objectID == 0)
			{
				i_objectID = a_constraint.getObjectID();
			}
			return a_candidate.i_key == i_objectID;
		}
	}
}
