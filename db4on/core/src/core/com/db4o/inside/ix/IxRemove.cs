namespace com.db4o.inside.ix
{
	/// <summary>A node to represent an entry removed from an Index</summary>
	public class IxRemove : com.db4o.inside.ix.IxPatch
	{
		public IxRemove(com.db4o.inside.ix.IndexTransaction a_ft, int a_parentID, object 
			a_value) : base(a_ft, a_parentID, a_value)
		{
			i_size = 0;
		}

		public override int ownSize()
		{
			return 0;
		}

		public override string ToString()
		{
			string str = "IxRemove " + i_parentID + "\n " + handler().comparableObject(trans(
				), i_value);
			return str;
		}

		public override void freespaceVisit(com.db4o.inside.freespace.FreespaceVisitor visitor
			, int index)
		{
		}

		public override void visit(com.db4o.foundation.Visitor4 visitor, int[] lowerAndUpperMatch
			)
		{
		}

		public override int write(com.db4o.inside.ix.Indexable4 a_handler, com.db4o.YapWriter
			 a_writer)
		{
			return 0;
		}

		public override void visitAll(com.db4o.foundation.IntObjectVisitor visitor)
		{
		}
	}
}
