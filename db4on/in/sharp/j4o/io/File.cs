/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;
using com.db4o;

namespace j4o.io
{
	public class File
	{	
		public static readonly char separator = Path.DirectorySeparatorChar;
		
		private string _path;

		public File(string path)
		{
			_path = path;
		}

		public File(string dir, string file)
		{
			if (dir == null)
			{
				_path = file;
			}
			else
			{
				_path = Path.Combine(dir, file);
			}
		}

		public virtual bool delete()
		{
			if (exists())
			{
				System.IO.File.Delete(_path);
				return !exists();
			}
			return false;
		}

		public bool exists()
		{
			return System.IO.File.Exists(_path) || Directory.Exists(_path);
		}

		public string getAbsolutePath()
		{
			return _path;
		}

		public string getName()
		{
			int index = _path.LastIndexOf(separator);
			return _path.Substring(index + 1);
		}

		public string getPath()
		{
			return _path;
		}

		public bool isDirectory()
		{
#if CF_1_0 || CF_2_0
			return System.IO.Directory.Exists(_path);
#else
			return (System.IO.File.GetAttributes(_path) & FileAttributes.Directory) != 0;
#endif
		}

		public long length()
		{
			return new FileInfo(_path).Length;
		}

		public string[] list()
		{
			return Directory.GetFiles(_path);
		}

		public bool mkdir()
		{
			if (exists())
			{
				return false;
			}
			Directory.CreateDirectory(_path);
			return exists();
		}

		public bool mkdirs()
		{
			if (exists())
			{
				return false;
			}
			int pos = _path.LastIndexOf(separator);
			if (pos > 0)
			{
				new File(_path.Substring(0, pos)).mkdirs();
			}
			return mkdir();
		}

		public void renameTo(File file)
		{
			new FileInfo(_path).MoveTo(file.getPath());
		}
	}
}