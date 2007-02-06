namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class SystemInfoFileImpl : com.db4o.ext.SystemInfo
	{
		private com.db4o.@internal.LocalObjectContainer _file;

		public SystemInfoFileImpl(com.db4o.@internal.LocalObjectContainer file)
		{
			_file = file;
		}

		public virtual int FreespaceEntryCount()
		{
			if (!HasFreespaceManager())
			{
				return 0;
			}
			return FreespaceManager().EntryCount();
		}

		private bool HasFreespaceManager()
		{
			return FreespaceManager() != null;
		}

		private com.db4o.@internal.freespace.FreespaceManager FreespaceManager()
		{
			return _file.FreespaceManager();
		}

		public virtual long FreespaceSize()
		{
			if (!HasFreespaceManager())
			{
				return 0;
			}
			long blockSize = _file.BlockSize();
			long blockedSize = FreespaceManager().FreeSize();
			return blockSize * blockedSize;
		}

		public virtual long TotalSize()
		{
			return _file.FileLength();
		}
	}
}
