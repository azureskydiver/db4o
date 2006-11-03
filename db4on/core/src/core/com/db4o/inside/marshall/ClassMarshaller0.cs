namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class ClassMarshaller0 : com.db4o.inside.marshall.ClassMarshaller
	{
		protected override void ReadIndex(com.db4o.YapStream stream, com.db4o.YapClass clazz
			, com.db4o.YapReader reader)
		{
			int indexID = reader.ReadInt();
			if (!stream.MaintainsIndices() || !(stream is com.db4o.YapFile))
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
				new com.db4o.inside.convert.conversions.ClassIndexesToBTrees_5_5().Convert((com.db4o.YapFile
					)stream, indexID, Btree(clazz));
				stream.SetDirtyInSystemTransaction(clazz);
			}
		}

		private com.db4o.inside.btree.BTree Btree(com.db4o.YapClass clazz)
		{
			return com.db4o.inside.classindex.BTreeClassIndexStrategy.Btree(clazz);
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
