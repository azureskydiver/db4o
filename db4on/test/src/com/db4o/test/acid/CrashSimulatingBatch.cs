using System.IO;

namespace com.db4o.test.acid
{
	public class CrashSimulatingBatch
	{
		internal com.db4o.foundation.Collection4 writes = new com.db4o.foundation.Collection4
			();

		internal com.db4o.foundation.Collection4 currentWrite = new com.db4o.foundation.Collection4
			();

		public virtual void add(byte[] bytes, long offset, int length)
		{
			currentWrite.add(new com.db4o.test.acid.CrashSimulatingWrite(bytes, offset, length
				));
		}

		public virtual void sync()
		{
			writes.add(currentWrite);
			currentWrite = new com.db4o.foundation.Collection4();
		}

		public virtual int numSyncs()
		{
			return writes.size();
		}

		public virtual int writeVersions(string file)
		{
			int count = 0;
			int rcount = 0;
			string lastFileName = file + "0";
			string rightFileName = file + "R";
			File.Copy(lastFileName,rightFileName);
			com.db4o.foundation.Iterator4 syncIter = writes.strictIterator();
			while (syncIter.hasNext())
			{
				com.db4o.foundation.Collection4 writesBetweenSync = (com.db4o.foundation.Collection4
					)syncIter.next();
				j4o.io.RandomAccessFile rightRaf = new j4o.io.RandomAccessFile(rightFileName, "rw"
					);
				com.db4o.foundation.Iterator4 singleForwardIter = writesBetweenSync.strictIterator
					();
				while (singleForwardIter.hasNext())
				{
					com.db4o.test.acid.CrashSimulatingWrite csw = (com.db4o.test.acid.CrashSimulatingWrite
						)singleForwardIter.next();
					csw.write(rightRaf);
				}
				rightRaf.close();
				com.db4o.foundation.Iterator4 singleBackwardIter = writesBetweenSync.iterator();
				while (singleBackwardIter.hasNext())
				{
					count++;
					com.db4o.test.acid.CrashSimulatingWrite csw = (com.db4o.test.acid.CrashSimulatingWrite
						)singleBackwardIter.next();
					string currentFileName = file + "W" + count;
					File.Copy(lastFileName,currentFileName);
					j4o.io.RandomAccessFile raf = new j4o.io.RandomAccessFile(currentFileName, "rw");
					csw.write(raf);
					raf.close();
					lastFileName = currentFileName;
				}
				rcount++;
				File.Copy(rightFileName,rightFileName + rcount);
				lastFileName = rightFileName;
			}
			return count;
		}
	}
}
