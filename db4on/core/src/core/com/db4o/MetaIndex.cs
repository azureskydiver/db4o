namespace com.db4o
{
	/// <summary>The index record that is written to the database file.</summary>
	/// <remarks>
	/// The index record that is written to the database file.
	/// Don't obfuscate.
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class MetaIndex : com.db4o.Internal4
	{
		public int indexAddress;

		public int indexEntries;

		public int indexLength;

		private const int patchAddress = 0;

		private const int patchEntries = 0;

		private const int patchLength = 0;

		public virtual void Read(com.db4o.YapReader reader)
		{
			indexAddress = reader.ReadInt();
			indexEntries = reader.ReadInt();
			indexLength = reader.ReadInt();
			reader.ReadInt();
			reader.ReadInt();
			reader.ReadInt();
		}

		public virtual void Write(com.db4o.YapWriter writer)
		{
			writer.WriteInt(indexAddress);
			writer.WriteInt(indexEntries);
			writer.WriteInt(indexLength);
			writer.WriteInt(patchAddress);
			writer.WriteInt(patchEntries);
			writer.WriteInt(patchLength);
		}

		public virtual void Free(com.db4o.YapFile file)
		{
			file.Free(indexAddress, indexLength);
			indexAddress = 0;
			indexLength = 0;
		}
	}
}
