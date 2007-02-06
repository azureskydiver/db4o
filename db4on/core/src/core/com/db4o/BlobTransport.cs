namespace com.db4o
{
	/// <exclude></exclude>
	public interface BlobTransport
	{
		void WriteBlobTo(com.db4o.@internal.Transaction trans, com.db4o.@internal.BlobImpl
			 blob, j4o.io.File file);

		void ReadBlobFrom(com.db4o.@internal.Transaction trans, com.db4o.@internal.BlobImpl
			 blob, j4o.io.File file);
	}
}
