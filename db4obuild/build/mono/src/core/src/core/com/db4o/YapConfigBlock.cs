/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <summary>
	/// configuration and agent to write the configuration block
	/// The configuration block also contains the timer lock and
	/// a pointer to the running transaction.
	/// </summary>
	/// <remarks>
	/// configuration and agent to write the configuration block
	/// The configuration block also contains the timer lock and
	/// a pointer to the running transaction.
	/// </remarks>
	internal sealed class YapConfigBlock : j4o.lang.Runnable
	{
		private readonly object _timeWriterLock = new object();

		private readonly com.db4o.YapFile _stream;

		internal int _address;

		private com.db4o.Transaction _transactionToCommit;

		internal int _bootRecordID;

		private const int POINTER_ADDRESS = 2;

		private const int MINIMUM_LENGTH = com.db4o.YapConst.YAPINT_LENGTH + (com.db4o.YapConst
			.YAPLONG_LENGTH * 2) + 1;

		internal const int OPEN_TIME_OFFSET = com.db4o.YapConst.YAPINT_LENGTH;

		internal const int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + com.db4o.YapConst.YAPLONG_LENGTH;

		internal const int TRANSACTION_OFFSET = MINIMUM_LENGTH;

		private const int BOOTRECORD_OFFSET = TRANSACTION_OFFSET + com.db4o.YapConst.YAPINT_LENGTH
			 * 2;

		private const int BLOCKLENGTH_OFFSET = BOOTRECORD_OFFSET + com.db4o.YapConst.YAPINT_LENGTH;

		private const int LENGTH = MINIMUM_LENGTH + (com.db4o.YapConst.YAPINT_LENGTH * 4);

		private readonly long _opentime;

		internal byte _encoding;

		internal YapConfigBlock(com.db4o.YapFile stream)
		{
			_stream = stream;
			_opentime = processID();
			if (lockFile())
			{
				writeHeaderLock();
			}
		}

		internal YapConfigBlock(com.db4o.YapFile file, byte encoding) : this(file)
		{
			_encoding = encoding;
		}

		private com.db4o.YapWriter openTimeIO()
		{
			com.db4o.YapWriter writer = _stream.getWriter(_stream.getTransaction(), _address, 
				com.db4o.YapConst.YAPLONG_LENGTH);
			writer.moveForward(OPEN_TIME_OFFSET);
			return writer;
		}

		private void openTimeOverWritten()
		{
			if (lockFile())
			{
				com.db4o.YapWriter bytes = openTimeIO();
				bytes.read();
				if (com.db4o.YLong.readLong(bytes) != _opentime)
				{
					com.db4o.Db4o.throwRuntimeException(22);
				}
				writeOpenTime();
			}
		}

		internal com.db4o.Transaction getTransactionToCommit()
		{
			return _transactionToCommit;
		}

		internal void go()
		{
			_stream.createStringIO(_encoding);
			if (lockFile())
			{
				try
				{
					writeAccessTime();
				}
				catch (System.Exception e)
				{
				}
				syncFiles();
				openTimeOverWritten();
				new j4o.lang.Thread(this).start();
			}
		}

		private com.db4o.YapWriter headerLockIO()
		{
			com.db4o.YapWriter writer = _stream.getWriter(_stream.getTransaction(), 0, com.db4o.YapConst
				.YAPINT_LENGTH);
			writer.moveForward(2 + com.db4o.YapConst.YAPINT_LENGTH);
			return writer;
		}

		private void headerLockOverwritten()
		{
			if (lockFile())
			{
				com.db4o.YapWriter bytes = headerLockIO();
				bytes.read();
				if (com.db4o.YInt.readInt(bytes) != ((int)_opentime))
				{
					throw new com.db4o.ext.DatabaseFileLockedException();
				}
				writeHeaderLock();
			}
		}

		private bool lockFile()
		{
			return _stream.needsLockFileThread();
		}

		internal static long processID()
		{
			long id = j4o.lang.JavaSystem.currentTimeMillis();
			return id;
		}

		/// <summary>returns true if Unicode check is necessary</summary>
		internal bool read(com.db4o.YapWriter reader)
		{
			_address = reader.readInt();
			if (_address == 2)
			{
				return true;
			}
			read();
			return false;
		}

		internal void read()
		{
			writeOpenTime();
			com.db4o.YapWriter reader = _stream.getWriter(_stream.getSystemTransaction(), _address
				, LENGTH);
			try
			{
				_stream.readBytes(reader._buffer, _address, LENGTH);
			}
			catch (System.Exception e)
			{
			}
			int oldLength = reader.readInt();
			if (oldLength > LENGTH || oldLength < MINIMUM_LENGTH)
			{
				com.db4o.Db4o.throwRuntimeException(17);
			}
			long lastOpenTime = com.db4o.YLong.readLong(reader);
			long lastAccessTime = com.db4o.YLong.readLong(reader);
			_encoding = reader.readByte();
			if (oldLength > TRANSACTION_OFFSET)
			{
				int transactionID1 = com.db4o.YInt.readInt(reader);
				int transactionID2 = com.db4o.YInt.readInt(reader);
				if ((transactionID1 > 0) && (transactionID1 == transactionID2))
				{
					_transactionToCommit = new com.db4o.Transaction(_stream, null);
					_transactionToCommit.setAddress(transactionID1);
				}
			}
			if (oldLength > BOOTRECORD_OFFSET)
			{
				_bootRecordID = com.db4o.YInt.readInt(reader);
			}
			if (oldLength > BLOCKLENGTH_OFFSET)
			{
				com.db4o.YInt.readInt(reader);
			}
			if (lockFile() && (lastAccessTime != 0))
			{
				_stream.logMsg(28, null);
				long waitTime = com.db4o.YapConst.LOCK_TIME_INTERVAL * 10;
				long currentTime = j4o.lang.JavaSystem.currentTimeMillis();
				while (j4o.lang.JavaSystem.currentTimeMillis() < currentTime + waitTime)
				{
					com.db4o.foundation.Cool.sleepIgnoringInterruption(waitTime);
				}
				reader = _stream.getWriter(_stream.getSystemTransaction(), _address, com.db4o.YapConst
					.YAPLONG_LENGTH * 2);
				reader.moveForward(OPEN_TIME_OFFSET);
				reader.read();
				long currentOpenTime = com.db4o.YLong.readLong(reader);
				long currentAccessTime = com.db4o.YLong.readLong(reader);
				if ((currentAccessTime > lastAccessTime))
				{
					throw new com.db4o.ext.DatabaseFileLockedException();
				}
			}
			if (lockFile())
			{
				com.db4o.foundation.Cool.sleepIgnoringInterruption(100);
				syncFiles();
				openTimeOverWritten();
			}
			if (oldLength < LENGTH)
			{
				write();
			}
			go();
		}

		public void run()
		{
			j4o.lang.Thread t = j4o.lang.Thread.currentThread();
			t.setName("db4o file lock");
			try
			{
				while (writeAccessTime())
				{
					com.db4o.foundation.Cool.sleepIgnoringInterruption(com.db4o.YapConst.LOCK_TIME_INTERVAL
						);
				}
			}
			catch (j4o.io.IOException e)
			{
			}
		}

		internal void syncFiles()
		{
			_stream.syncFiles();
		}

		internal void write()
		{
			headerLockOverwritten();
			_address = _stream.getSlot(LENGTH);
			com.db4o.YapWriter writer = _stream.getWriter(_stream.i_trans, _address, LENGTH);
			com.db4o.YInt.writeInt(LENGTH, writer);
			com.db4o.YLong.writeLong(_opentime, writer);
			com.db4o.YLong.writeLong(_opentime, writer);
			writer.append(_encoding);
			com.db4o.YInt.writeInt(0, writer);
			com.db4o.YInt.writeInt(0, writer);
			com.db4o.YInt.writeInt(_bootRecordID, writer);
			com.db4o.YInt.writeInt(0, writer);
			writer.write();
			writePointer();
		}

		internal bool writeAccessTime()
		{
			return _stream.writeAccessTime();
		}

		private void writeOpenTime()
		{
			if (lockFile())
			{
				com.db4o.YapWriter writer = openTimeIO();
				com.db4o.YLong.writeLong(_opentime, writer);
				writer.write();
			}
		}

		private void writeHeaderLock()
		{
			if (lockFile())
			{
				com.db4o.YapWriter writer = headerLockIO();
				com.db4o.YInt.writeInt(((int)_opentime), writer);
				writer.write();
			}
		}

		private void writePointer()
		{
			headerLockOverwritten();
			com.db4o.YapWriter writer = _stream.getWriter(_stream.i_trans, 0, com.db4o.YapConst
				.YAPID_LENGTH);
			writer.moveForward(2);
			com.db4o.YInt.writeInt(_address, writer);
			if (com.db4o.Deploy.debug && com.db4o.Deploy.overwrite)
			{
				writer.setID(com.db4o.YapConst.IGNORE_ID);
			}
			writer.write();
			writeHeaderLock();
		}
	}
}
