namespace com.db4o.@internal.convert.conversions
{
	/// <exclude></exclude>
	public class ClassIndexesToBTrees_5_5 : com.db4o.@internal.convert.Conversion
	{
		public const int VERSION = 5;

		public virtual void Convert(com.db4o.@internal.LocalObjectContainer yapFile, int 
			classIndexId, com.db4o.@internal.btree.BTree bTree)
		{
			com.db4o.@internal.Transaction trans = yapFile.GetSystemTransaction();
			com.db4o.@internal.Buffer reader = yapFile.ReadReaderByID(trans, classIndexId);
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

		public override void Convert(com.db4o.@internal.convert.ConversionStage.SystemUpStage
			 stage)
		{
			stage.File().StoredClasses();
		}
	}
}
