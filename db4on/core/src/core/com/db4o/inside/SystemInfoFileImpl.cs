namespace com.db4o.inside
{
	/// <exclude></exclude>
	public class SystemInfoFileImpl : com.db4o.ext.SystemInfo
	{
		private com.db4o.YapFile _file;

		public SystemInfoFileImpl(com.db4o.YapFile file)
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

		private com.db4o.inside.freespace.FreespaceManager FreespaceManager()
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
	}
}
