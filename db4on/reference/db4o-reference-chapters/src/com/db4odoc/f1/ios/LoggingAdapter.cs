/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System.IO;
namespace com.db4odoc.f1.ios
{
	public class LoggingAdapter : com.db4o.io.IoAdapter
	{
		private j4o.io.RandomAccessFile _delegate;
		
		public LoggingAdapter()
		{
		}

		protected internal LoggingAdapter(string path, bool lockFile, long initialLength)
		{
			_delegate = new j4o.io.RandomAccessFile(path, "rw");
			if (initialLength > 0)
			{
				_delegate.Seek(initialLength - 1);
				_delegate.Write(new byte[] { 0 });
			}
		}

		public void SetOut(TextWriter outs)
		{
			System.Console.SetOut(outs);
		}

		public override void Close()
		{
			System.Console.WriteLine("Closing file");
			_delegate.Close();
		}

		public override void Delete(string path)
		{
			System.Console.WriteLine("Deleting file " + path);
			new j4o.io.File(path).Delete();
		}

		public override bool Exists(string path)
		{
			j4o.io.File existingFile = new j4o.io.File(path);
			return existingFile.Exists() && existingFile.Length() > 0;
		}

		public override long GetLength()
		{
			System.Console.WriteLine("File length:" + _delegate.Length());
			return _delegate.Length();
		}

		public override com.db4o.io.IoAdapter Open(string path, bool lockFile, long initialLength)
		{
			System.Console.WriteLine("Opening file " + path);
			return new LoggingAdapter(path, lockFile, initialLength);
		}

		public override int Read(byte[] bytes, int length)
		{
			System.Console.WriteLine("Reading " + length + " bytes");
			return _delegate.Read(bytes, 0, length);
		}

		public override void Seek(long pos)
		{
			System.Console.WriteLine("Setting pointer position to  " + pos);
			_delegate.Seek(pos);
		}

		public override void Sync()
		{
			System.Console.WriteLine("Synchronizing");
			_delegate.GetFD().Sync();
		}

		public override void Write(byte[] buffer, int length)
		{
			System.Console.WriteLine("Writing " + length + " bytes");
			_delegate.Write(buffer, 0, length);
		}
	}
}
