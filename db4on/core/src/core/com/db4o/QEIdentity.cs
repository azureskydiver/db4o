namespace com.db4o
{
	/// <exclude></exclude>
	public class QEIdentity : com.db4o.QEEqual
	{
		public int i_objectID;

		public override bool Identity()
		{
			return true;
		}

		internal override bool Evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (i_objectID == 0)
			{
				i_objectID = a_constraint.GetObjectID();
			}
			return a_candidate._key == i_objectID;
		}
	}
}
