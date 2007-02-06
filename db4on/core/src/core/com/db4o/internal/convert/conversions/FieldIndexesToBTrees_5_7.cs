namespace com.db4o.@internal.convert.conversions
{
	/// <exclude></exclude>
	public class FieldIndexesToBTrees_5_7 : com.db4o.@internal.convert.Conversion
	{
		public const int VERSION = 6;

		public override void Convert(com.db4o.@internal.convert.ConversionStage.SystemUpStage
			 stage)
		{
			stage.File().ClassCollection().WriteAllClasses();
			RebuildUUIDIndex(stage.File());
			FreeOldUUIDMetaIndex(stage.File());
		}

		private void RebuildUUIDIndex(com.db4o.@internal.LocalObjectContainer file)
		{
			com.db4o.@internal.UUIDFieldMetadata uuid = file.GetUUIDIndex();
			com.db4o.@internal.ClassMetadataIterator i = file.ClassCollection().Iterator();
			while (i.MoveNext())
			{
				com.db4o.@internal.ClassMetadata clazz = i.CurrentClass();
				if (clazz.GenerateUUIDs())
				{
					uuid.RebuildIndexForClass(file, clazz);
				}
			}
		}

		private void FreeOldUUIDMetaIndex(com.db4o.@internal.LocalObjectContainer file)
		{
			com.db4o.@internal.fileheader.FileHeader fh = file.GetFileHeader();
			if (!(fh is com.db4o.@internal.fileheader.FileHeader0))
			{
				return;
			}
			com.db4o.MetaIndex metaIndex = ((com.db4o.@internal.fileheader.FileHeader0)fh).GetUUIDMetaIndex
				();
			if (metaIndex == null)
			{
				return;
			}
			file.Free(metaIndex.indexAddress, metaIndex.indexLength);
		}
	}
}
