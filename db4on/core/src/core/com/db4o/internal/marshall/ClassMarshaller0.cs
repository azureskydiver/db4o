namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public class ClassMarshaller0 : com.db4o.@internal.marshall.ClassMarshaller
	{
		protected override void ReadIndex(com.db4o.@internal.ObjectContainerBase stream, 
			com.db4o.@internal.ClassMetadata clazz, com.db4o.@internal.Buffer reader)
		{
			int indexID = reader.ReadInt();
			if (!stream.MaintainsIndices() || !(stream is com.db4o.@internal.LocalObjectContainer
				))
			{
				return;
			}
			if (Btree(clazz) != null)
			{
				return;
			}
			clazz.Index().Read(stream, ValidIndexId(indexID));
			if (IsOldClassIndex(indexID))
			{
				new com.db4o.@internal.convert.conversions.ClassIndexesToBTrees_5_5().Convert((com.db4o.@internal.LocalObjectContainer
					)stream, indexID, Btree(clazz));
				stream.SetDirtyInSystemTransaction(clazz);
			}
		}

		private com.db4o.@internal.btree.BTree Btree(com.db4o.@internal.ClassMetadata clazz
			)
		{
			return com.db4o.@internal.classindex.BTreeClassIndexStrategy.Btree(clazz);
		}

		private int ValidIndexId(int indexID)
		{
			return IsOldClassIndex(indexID) ? 0 : -indexID;
		}

		private bool IsOldClassIndex(int indexID)
		{
			return indexID > 0;
		}

		protected override int IndexIDForWriting(int indexID)
		{
			return indexID;
		}
	}
}
