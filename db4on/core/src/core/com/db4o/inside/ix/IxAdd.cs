namespace com.db4o.inside.ix
{
	/// <summary>An addition to a field index.</summary>
	/// <remarks>An addition to a field index.</remarks>
	public class IxAdd : com.db4o.inside.ix.IxPatch
	{
		internal bool _keepRemoved;

		public IxAdd(com.db4o.inside.ix.IndexTransaction a_ft, int a_parentID, object a_value
			) : base(a_ft, a_parentID, a_value)
		{
		}

		internal override void beginMerge()
		{
			base.beginMerge();
			handler().prepareComparison(handler().comparableObject(trans(), _value));
		}

		public override void visit(object obj)
		{
			((com.db4o.foundation.Visitor4)obj).visit(_parentID);
		}

		public override void visit(com.db4o.foundation.Visitor4 visitor, int[] lowerAndUpperMatch
			)
		{
			visitor.visit(_parentID);
		}

		public override void freespaceVisit(com.db4o.inside.freespace.FreespaceVisitor visitor
			, int index)
		{
			visitor.visit(_parentID, ((int)_value));
		}

		public override int write(com.db4o.inside.ix.Indexable4 a_handler, com.db4o.YapWriter
			 a_writer)
		{
			a_handler.writeIndexEntry(a_writer, _value);
			a_writer.writeInt(_parentID);
			a_writer.writeForward();
			return 1;
		}

		public override string ToString()
		{
			return base.ToString();
			string str = "IxAdd " + _parentID + "\n " + handler().comparableObject(trans(), _value
				);
			return str;
		}

		public override void visitAll(com.db4o.foundation.IntObjectVisitor visitor)
		{
			visitor.visit(_parentID, handler().comparableObject(trans(), _value));
		}

		public override object shallowClone()
		{
			com.db4o.inside.ix.IxAdd add = new com.db4o.inside.ix.IxAdd(_fieldTransaction, _parentID
				, _value);
			base.shallowCloneInternal(add);
			add._keepRemoved = _keepRemoved;
			return add;
		}
	}
}
