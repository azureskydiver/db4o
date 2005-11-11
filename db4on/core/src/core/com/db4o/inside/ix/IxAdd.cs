namespace com.db4o.inside.ix
{
	/// <summary>An addition to a field index.</summary>
	/// <remarks>An addition to a field index.</remarks>
	public class IxAdd : com.db4o.inside.ix.IxPatch
	{
		internal bool i_keepRemoved;

		public IxAdd(com.db4o.inside.ix.IndexTransaction a_ft, int a_parentID, object a_value
			) : base(a_ft, a_parentID, a_value)
		{
		}

		internal override void beginMerge()
		{
			base.beginMerge();
			handler().prepareComparison(handler().comparableObject(trans(), i_value));
		}

		public override void visit(object obj)
		{
			((com.db4o.foundation.Visitor4)obj).visit(i_parentID);
		}

		public override void visit(com.db4o.foundation.Visitor4 visitor, int[] lowerAndUpperMatch
			)
		{
			visitor.visit(i_parentID);
		}

		public override void freespaceVisit(com.db4o.inside.freespace.FreespaceVisitor visitor
			, int index)
		{
			visitor.visit(i_parentID, ((int)i_value));
		}

		public override int write(com.db4o.inside.ix.Indexable4 a_handler, com.db4o.YapWriter
			 a_writer)
		{
			a_handler.writeIndexEntry(a_writer, i_value);
			a_writer.writeInt(i_parentID);
			a_writer.writeForward();
			return 1;
		}

		public override string ToString()
		{
			string str = "IxAdd " + i_parentID + "\n " + handler().comparableObject(trans(), 
				i_value);
			return str;
		}

		public override void visitAll(com.db4o.foundation.IntObjectVisitor visitor)
		{
			visitor.visit(i_parentID, handler().comparableObject(trans(), i_value));
		}
	}
}
