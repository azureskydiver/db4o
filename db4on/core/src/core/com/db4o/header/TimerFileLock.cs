namespace com.db4o.header
{
	/// <exclude></exclude>
	public abstract class TimerFileLock : j4o.lang.Runnable
	{
		public static com.db4o.header.TimerFileLock ForFile(com.db4o.YapFile file)
		{
			if (LockFile(file))
			{
				return new com.db4o.header.TimerFileLockEnabled(file);
			}
			return new com.db4o.header.TimerFileLockDisabled();
		}

		private static bool LockFile(com.db4o.YapFile file)
		{
			return file.NeedsLockFileThread();
		}

		public abstract void CheckHeaderLock();

		public abstract void CheckOpenTime();

		public abstract bool LockFile();

		public abstract long OpenTime();

		public abstract void SetAddresses(int baseAddress, int openTimeOffset, int accessTimeOffset
			);

		public abstract void Start();

		public abstract void WriteHeaderLock();

		public abstract void WriteOpenTime();

		public abstract void Close();

		public abstract void Run();
	}
}
