namespace com.db4o.inside.convert.conversions
{
	/// <exclude></exclude>
	public class FieldIndexesToBTrees_5_7 : com.db4o.inside.convert.Conversion
	{
		public const int VERSION = 6;

		public override void Convert(com.db4o.inside.convert.ConversionStage.ClassCollectionAvailableStage
			 stage)
		{
			base.Convert(stage);
		}

		public override void Convert(com.db4o.inside.convert.ConversionStage.SystemUpStage
			 stage)
		{
			stage.File().ClassCollection().WriteAllClasses();
			RebuildUUIDIndex(stage.File());
			FreeOldUUIDMetaIndex(stage.File());
		}

		private void RebuildUUIDIndex(com.db4o.YapFile file)
		{
			com.db4o.YapFieldUUID uuid = file.GetFieldUUID();
			com.db4o.YapClassCollectionIterator i = file.ClassCollection().Iterator();
			while (i.MoveNext())
			{
				com.db4o.YapClass clazz = i.CurrentClass();
				if (clazz.GenerateUUIDs())
				{
					uuid.RebuildIndexForClass(file, clazz);
				}
			}
		}

		private void FreeOldUUIDMetaIndex(com.db4o.YapFile file)
		{
			com.db4o.header.FileHeader fh = file.GetFileHeader();
			if (!(fh is com.db4o.header.FileHeader0))
			{
				return;
			}
			com.db4o.MetaIndex metaIndex = ((com.db4o.header.FileHeader0)fh).GetUUIDMetaIndex
				();
			if (metaIndex == null)
			{
				return;
			}
			file.Free(metaIndex.indexAddress, metaIndex.indexLength);
		}
	}
}
