/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System.IO;

namespace Db4objects.Db4odoc.IOs
{
	public class LoggingAdapter : Db4objects.Db4o.IO.IoAdapter {
		private Sharpen.IO.RandomAccessFile _delegate;
		
		public LoggingAdapter()
		{
		}

		protected internal LoggingAdapter(string path, bool lockFile, long initialLength)
		{
            _delegate = new Sharpen.IO.RandomAccessFile(path, "rw");
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
			new Sharpen.IO.File(path).Delete();
		}

		public override bool Exists(string path)
		{
            Sharpen.IO.File existingFile = new Sharpen.IO.File(path);
			return existingFile.Exists() && existingFile.Length() > 0;
		}

		public override long GetLength()
		{
			System.Console.WriteLine("File length:" + _delegate.Length());
			return _delegate.Length();
		}

		public override Db4objects.Db4o .IO.IoAdapter  Open(string path, bool lockFile, long initialLength)
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
