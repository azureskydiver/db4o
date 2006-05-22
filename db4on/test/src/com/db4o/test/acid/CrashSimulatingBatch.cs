﻿using System.IO;

namespace com.db4o.test.acid
{
	public class CrashSimulatingBatch
	{
		internal com.db4o.foundation.Collection4 writes = new com.db4o.foundation.Collection4
			();

		internal com.db4o.foundation.Collection4 currentWrite = new com.db4o.foundation.Collection4
			();

		public virtual void Add(byte[] bytes, long offset, int length)
		{
			currentWrite.Add(new com.db4o.test.acid.CrashSimulatingWrite(bytes, offset, length
				));
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
			CopyFile(lastFileName,rightFileName);
			com.db4o.foundation.Iterator4 syncIter = writes.StrictIterator();
			while (syncIter.HasNext())
			{
				com.db4o.foundation.Collection4 writesBetweenSync = (com.db4o.foundation.Collection4
					)syncIter.Next();
				j4o.io.RandomAccessFile rightRaf = new j4o.io.RandomAccessFile(rightFileName, "rw"
					);
				com.db4o.foundation.Iterator4 singleForwardIter = writesBetweenSync.StrictIterator
					();
				while (singleForwardIter.HasNext())
				{
					com.db4o.test.acid.CrashSimulatingWrite csw = (com.db4o.test.acid.CrashSimulatingWrite
						)singleForwardIter.Next();
					csw.Write(rightRaf);
				}
				rightRaf.Close();
				com.db4o.foundation.Iterator4 singleBackwardIter = writesBetweenSync.Iterator();
				while (singleBackwardIter.HasNext())
				{
					count++;
					com.db4o.test.acid.CrashSimulatingWrite csw = (com.db4o.test.acid.CrashSimulatingWrite
						)singleBackwardIter.Next();
					string currentFileName = file + "W" + count;
					CopyFile(lastFileName,currentFileName);
					j4o.io.RandomAccessFile raf = new j4o.io.RandomAccessFile(currentFileName, "rw");
					csw.Write(raf);
					raf.Close();
					lastFileName = currentFileName;
				}
				rcount++;
				CopyFile(rightFileName,rightFileName + rcount);
				lastFileName = rightFileName;
			}
			return count;
		}
		
		private void CopyFile(string from,string to) 
		{
			File.Delete(to);
			File.Copy(from,to);
		}
	}
}
