namespace com.db4o.test.acid
{
	public class CrashSimulatingWrite
	{
		internal byte[] data;

		internal long offset;

		internal int length;

		public CrashSimulatingWrite(byte[] data, long offset, int length)
		{
			this.data = data;
			this.offset = offset;
			this.length = length;
		}

		public virtual void Write(j4o.io.RandomAccessFile raf)
		{
			raf.Seek(offset);
			raf.Write(data, 0, length);
		}
	}
}
