
namespace com.db4o
{
	internal class YapIndexes
	{
		internal readonly com.db4o.YapFieldVersion i_fieldVersion;

		internal readonly com.db4o.YapFieldUUID i_fieldUUID;

		internal YapIndexes(com.db4o.YapStream stream)
		{
			i_fieldVersion = new com.db4o.YapFieldVersion(stream);
			i_fieldUUID = new com.db4o.YapFieldUUID(stream);
		}
	}
}
