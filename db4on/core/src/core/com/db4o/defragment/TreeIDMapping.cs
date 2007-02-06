namespace com.db4o.defragment
{
	/// <summary>In-memory mapping for IDs during a defragmentation run.</summary>
	/// <remarks>In-memory mapping for IDs during a defragmentation run.</remarks>
	/// <seealso cref="com.db4o.defragment.Defragment">com.db4o.defragment.Defragment</seealso>
	public class TreeIDMapping : com.db4o.defragment.AbstractContextIDMapping
	{
		private com.db4o.foundation.Tree _tree;

		public override int MappedID(int oldID, bool lenient)
		{
			int classID = MappedClassID(oldID);
			if (classID != 0)
			{
				return classID;
			}
			com.db4o.@internal.TreeIntObject res = (com.db4o.@internal.TreeIntObject)com.db4o.@internal.TreeInt
				.Find(_tree, oldID);
			if (res != null)
			{
				return ((int)res._object);
			}
			if (lenient)
			{
				com.db4o.@internal.TreeIntObject nextSmaller = (com.db4o.@internal.TreeIntObject)
					com.db4o.foundation.Tree.FindSmaller(_tree, new com.db4o.@internal.TreeInt(oldID
					));
				if (nextSmaller != null)
				{
					int baseOldID = nextSmaller._key;
					int baseNewID = ((int)nextSmaller._object);
					return baseNewID + oldID - baseOldID;
				}
			}
			return 0;
		}

		public override void Open()
		{
		}

		public override void Close()
		{
		}

		protected override void MapNonClassIDs(int origID, int mappedID)
		{
			_tree = com.db4o.foundation.Tree.Add(_tree, new com.db4o.@internal.TreeIntObject(
				origID, mappedID));
		}
	}
}
