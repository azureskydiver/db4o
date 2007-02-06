namespace com.db4o.@internal
{
	internal class SharedIndexedFields
	{
		internal readonly com.db4o.@internal.VersionFieldMetadata i_fieldVersion;

		internal readonly com.db4o.@internal.UUIDFieldMetadata i_fieldUUID;

		internal SharedIndexedFields(com.db4o.@internal.ObjectContainerBase stream)
		{
			i_fieldVersion = new com.db4o.@internal.VersionFieldMetadata(stream);
			i_fieldUUID = new com.db4o.@internal.UUIDFieldMetadata(stream);
		}
	}
}
