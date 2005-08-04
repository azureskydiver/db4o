namespace com.db4o
{
	/// <summary>An addition to a field index.</summary>
	/// <remarks>An addition to a field index.</remarks>
	internal class IxAdd : com.db4o.IxPatch
	{
		internal bool i_keepRemoved;

		internal IxAdd(com.db4o.IxFieldTransaction a_ft, int a_parentID, object a_value) : 
			base(a_ft, a_parentID, a_value)
		{
		}

		internal override void beginMerge()
		{
			base.beginMerge();
			handler().prepareComparison(handler().comparableObject(trans(), i_value));
		}

		public override void visit(com.db4o.foundation.Visitor4 visitor, int[] lowerAndUpperMatch
			)
		{
			visitor.visit(i_parentID);
		}

		internal override void write(com.db4o.YapDataType a_handler, com.db4o.YapWriter a_writer
			)
		{
			a_handler.writeIndexEntry(a_writer, i_value);
			a_writer.writeInt(i_parentID);
			a_writer.writeForward();
		}

		public override string ToString()
		{
			string str = "IxAdd " + i_parentID + "\n " + handler().comparableObject(trans(), 
				i_value);
			return str;
		}
	}
}
