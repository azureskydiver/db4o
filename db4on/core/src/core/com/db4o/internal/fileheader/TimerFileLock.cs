namespace com.db4o.@internal.fileheader
{
	/// <exclude></exclude>
	public abstract class TimerFileLock : j4o.lang.Runnable
	{
		public static com.db4o.@internal.fileheader.TimerFileLock ForFile(com.db4o.@internal.LocalObjectContainer
			 file)
		{
			if (file.NeedsLockFileThread())
			{
				return new com.db4o.@internal.fileheader.TimerFileLockEnabled((com.db4o.@internal.IoAdaptedObjectContainer
					)file);
			}
			return new com.db4o.@internal.fileheader.TimerFileLockDisabled();
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
