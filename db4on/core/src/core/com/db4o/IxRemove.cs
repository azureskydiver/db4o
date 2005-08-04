namespace com.db4o
{
	/// <summary>A node to represent an entry removed from an Index</summary>
	internal class IxRemove : com.db4o.IxPatch
	{
		internal IxRemove(com.db4o.IxFieldTransaction a_ft, int a_parentID, object a_value
			) : base(a_ft, a_parentID, a_value)
		{
			i_size = 0;
		}

		internal override int ownSize()
		{
			return 0;
		}

		public override string ToString()
		{
			string str = "IxRemove " + i_parentID + "\n " + handler().comparableObject(trans(
				), i_value);
			return str;
		}

		public override void visit(com.db4o.foundation.Visitor4 visitor, int[] lowerAndUpperMatch
			)
		{
		}

		internal override void write(com.db4o.YapDataType a_handler, com.db4o.YapWriter a_writer
			)
		{
		}
	}
}
