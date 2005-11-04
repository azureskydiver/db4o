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

		public int patchAddress;

		public int patchEntries;

		public int patchLength;

		public virtual void read(com.db4o.YapReader reader)
		{
			indexAddress = reader.readInt();
			indexEntries = reader.readInt();
			indexLength = reader.readInt();
			patchAddress = reader.readInt();
			patchEntries = reader.readInt();
			patchLength = reader.readInt();
		}

		public virtual void write(com.db4o.YapWriter writer)
		{
			writer.writeInt(indexAddress);
			writer.writeInt(indexEntries);
			writer.writeInt(indexLength);
			writer.writeInt(patchAddress);
			writer.writeInt(patchEntries);
			writer.writeInt(patchLength);
		}

		public virtual void free(com.db4o.YapFile file)
		{
			file.free(indexAddress, indexLength);
			file.free(patchAddress, patchLength);
			indexAddress = 0;
			indexLength = 0;
			patchAddress = 0;
			patchLength = 0;
		}
	}
}
