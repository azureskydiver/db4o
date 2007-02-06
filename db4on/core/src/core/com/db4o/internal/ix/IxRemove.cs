namespace com.db4o.@internal.ix
{
	/// <summary>A node to represent an entry removed from an Index</summary>
	public class IxRemove : com.db4o.@internal.ix.IxPatch
	{
		public IxRemove(com.db4o.@internal.ix.IndexTransaction a_ft, int a_parentID, object
			 a_value) : base(a_ft, a_parentID, a_value)
		{
			_size = 0;
		}

		public override int OwnSize()
		{
			return 0;
		}

		public override string ToString()
		{
			return base.ToString();
			string str = "IxRemove " + _parentID + "\n " + Handler().ComparableObject(Trans()
				, _value);
			return str;
		}

		public override void FreespaceVisit(com.db4o.@internal.freespace.FreespaceVisitor
			 visitor, int index)
		{
		}

		public override void Visit(object obj)
		{
		}

		public override void Visit(com.db4o.foundation.Visitor4 visitor, int[] lowerAndUpperMatch
			)
		{
		}

		public override int Write(com.db4o.@internal.ix.Indexable4 a_handler, com.db4o.@internal.StatefulBuffer
			 a_writer)
		{
			return 0;
		}

		public override void VisitAll(com.db4o.foundation.IntObjectVisitor visitor)
		{
		}

		public override object ShallowClone()
		{
			com.db4o.@internal.ix.IxRemove remove = new com.db4o.@internal.ix.IxRemove(_fieldTransaction
				, _parentID, _value);
			base.ShallowCloneInternal(remove);
			return remove;
		}
	}
}
