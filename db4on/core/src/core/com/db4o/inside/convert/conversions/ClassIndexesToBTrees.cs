namespace com.db4o.inside.convert.conversions
{
	/// <exclude></exclude>
	public class ClassIndexesToBTrees : com.db4o.inside.convert.Conversion
	{
		public override void Run()
		{
			_yapFile.StoredClasses();
		}

		public virtual void Convert(com.db4o.YapFile yapFile, int classIndexId, com.db4o.inside.btree.BTree
			 bTree)
		{
			com.db4o.Transaction trans = yapFile.GetSystemTransaction();
			com.db4o.YapReader reader = yapFile.ReadReaderByID(trans, classIndexId);
			if (reader == null)
			{
				return;
			}
			int entries = reader.ReadInt();
			for (int i = 0; i < entries; i++)
			{
				bTree.Add(trans, reader.ReadInt());
			}
		}
	}
}
