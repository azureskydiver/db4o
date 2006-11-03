namespace com.db4o.db4ounit.common.acid
{
	public class CrashSimulatingBatch
	{
		internal com.db4o.foundation.Collection4 writes = new com.db4o.foundation.Collection4
			();

		internal com.db4o.foundation.Collection4 currentWrite = new com.db4o.foundation.Collection4
			();

		public virtual void Add(byte[] bytes, long offset, int length)
		{
			currentWrite.Add(new com.db4o.db4ounit.common.acid.CrashSimulatingWrite(bytes, offset
				, length));
		}

		public virtual void Sync()
		{
			writes.Add(currentWrite);
			currentWrite = new com.db4o.foundation.Collection4();
		}

		public virtual int NumSyncs()
		{
			return writes.Size();
		}

		public virtual int WriteVersions(string file)
		{
			int count = 0;
			int rcount = 0;
			string lastFileName = file + "0";
			string rightFileName = file + "R";
			com.db4o.db4ounit.util.File4.Copy(lastFileName, rightFileName);
			System.Collections.IEnumerator syncIter = writes.GetEnumerator();
			while (syncIter.MoveNext())
			{
				rcount++;
				com.db4o.foundation.Collection4 writesBetweenSync = (com.db4o.foundation.Collection4
					)syncIter.Current;
				j4o.io.RandomAccessFile rightRaf = new j4o.io.RandomAccessFile(rightFileName, "rw"
					);
				System.Collections.IEnumerator singleForwardIter = writesBetweenSync.GetEnumerator
					();
				while (singleForwardIter.MoveNext())
				{
					com.db4o.db4ounit.common.acid.CrashSimulatingWrite csw = (com.db4o.db4ounit.common.acid.CrashSimulatingWrite
						)singleForwardIter.Current;
					csw.Write(rightRaf);
				}
				rightRaf.Close();
				System.Collections.IEnumerator singleBackwardIter = writesBetweenSync.GetEnumerator
					();
				while (singleBackwardIter.MoveNext())
				{
					count++;
					com.db4o.db4ounit.common.acid.CrashSimulatingWrite csw = (com.db4o.db4ounit.common.acid.CrashSimulatingWrite
						)singleBackwardIter.Current;
					string currentFileName = file + "W" + count;
					com.db4o.db4ounit.util.File4.Copy(lastFileName, currentFileName);
					j4o.io.RandomAccessFile raf = new j4o.io.RandomAccessFile(currentFileName, "rw");
					csw.Write(raf);
					raf.Close();
					lastFileName = currentFileName;
				}
				com.db4o.db4ounit.util.File4.Copy(rightFileName, rightFileName + rcount);
				lastFileName = rightFileName;
			}
			return count;
		}
	}
}
