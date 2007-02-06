namespace com.db4o.@internal.ix
{
	/// <summary>An addition to a field index.</summary>
	/// <remarks>An addition to a field index.</remarks>
	public class IxAdd : com.db4o.@internal.ix.IxPatch
	{
		internal bool _keepRemoved;

		public IxAdd(com.db4o.@internal.ix.IndexTransaction a_ft, int a_parentID, object 
			a_value) : base(a_ft, a_parentID, a_value)
		{
		}

		internal override void BeginMerge()
		{
			base.BeginMerge();
			Handler().PrepareComparison(Handler().ComparableObject(Trans(), _value));
		}

		public override void Visit(object obj)
		{
			((com.db4o.foundation.Visitor4)obj).Visit(_parentID);
		}

		public override void Visit(com.db4o.foundation.Visitor4 visitor, int[] lowerAndUpperMatch
			)
		{
			visitor.Visit(_parentID);
		}

		public override void FreespaceVisit(com.db4o.@internal.freespace.FreespaceVisitor
			 visitor, int index)
		{
			visitor.Visit(_parentID, ((int)_value));
		}

		public override int Write(com.db4o.@internal.ix.Indexable4 a_handler, com.db4o.@internal.StatefulBuffer
			 a_writer)
		{
			a_handler.WriteIndexEntry(a_writer, _value);
			a_writer.WriteInt(_parentID);
			a_writer.WriteForward();
			return 1;
		}

		public override string ToString()
		{
			return base.ToString();
			string str = "IxAdd " + _parentID + "\n " + Handler().ComparableObject(Trans(), _value
				);
			return str;
		}

		public override void VisitAll(com.db4o.foundation.IntObjectVisitor visitor)
		{
			visitor.Visit(_parentID, Handler().ComparableObject(Trans(), _value));
		}

		public override object ShallowClone()
		{
			com.db4o.@internal.ix.IxAdd add = new com.db4o.@internal.ix.IxAdd(_fieldTransaction
				, _parentID, _value);
			base.ShallowCloneInternal(add);
			add._keepRemoved = _keepRemoved;
			return add;
		}
	}
}
