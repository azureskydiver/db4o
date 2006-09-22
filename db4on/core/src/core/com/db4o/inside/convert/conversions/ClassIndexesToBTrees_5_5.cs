namespace com.db4o.inside.convert.conversions
{
	/// <exclude></exclude>
	public class ClassIndexesToBTrees_5_5 : com.db4o.inside.convert.Conversion
	{
		public const int VERSION = 5;

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

		public override void Convert(com.db4o.inside.convert.ConversionStage.SystemUpStage
			 stage)
		{
			stage.File().StoredClasses();
		}
	}
}
