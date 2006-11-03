namespace com.db4o
{
	/// <summary>
	/// Transfer of blobs to and from the db4o system,
	/// if users use the Blob Db4oType.
	/// </summary>
	/// <remarks>
	/// Transfer of blobs to and from the db4o system,
	/// if users use the Blob Db4oType.
	/// </remarks>
	/// <exclude></exclude>
	public class BlobImpl : com.db4o.types.Blob, j4o.lang.Cloneable, com.db4o.Db4oTypeImpl
	{
		public const int COPYBUFFER_LENGTH = 4096;

		public string fileName;

		public string i_ext;

		[com.db4o.Transient]
		private j4o.io.File i_file;

		[com.db4o.Transient]
		private com.db4o.BlobStatus i_getStatusFrom;

		public int i_length;

		[com.db4o.Transient]
		private double i_status = com.db4o.ext.Status.UNUSED;

		[com.db4o.Transient]
		private com.db4o.YapStream i_stream;

		[com.db4o.Transient]
		private com.db4o.Transaction i_trans;

		public virtual int AdjustReadDepth(int a_depth)
		{
			return 1;
		}

		public virtual bool CanBind()
		{
			return true;
		}

		private string CheckExt(j4o.io.File file)
		{
			string name = file.GetName();
			int pos = name.LastIndexOf(".");
			if (pos > 0)
			{
				i_ext = j4o.lang.JavaSystem.Substring(name, pos);
				return j4o.lang.JavaSystem.Substring(name, 0, pos);
			}
			i_ext = string.Empty;
			return name;
		}

		private void Copy(j4o.io.File from, j4o.io.File to)
		{
			to.Delete();
			j4o.io.BufferedInputStream @in = new j4o.io.BufferedInputStream(new j4o.io.FileInputStream
				(from));
			j4o.io.BufferedOutputStream @out = new j4o.io.BufferedOutputStream(new j4o.io.FileOutputStream
				(to));
			byte[] buffer = new byte[COPYBUFFER_LENGTH];
			int bytesread = -1;
			while ((bytesread = @in.Read(buffer)) >= 0)
			{
				@out.Write(buffer, 0, bytesread);
			}
			@out.Flush();
			@out.Close();
			@in.Close();
		}

		public virtual object CreateDefault(com.db4o.Transaction a_trans)
		{
			com.db4o.BlobImpl bi = null;
			try
			{
				bi = (com.db4o.BlobImpl)this.MemberwiseClone();
				bi.SetTrans(a_trans);
			}
			catch (j4o.lang.CloneNotSupportedException)
			{
				return null;
			}
			return bi;
		}

		public virtual j4o.io.FileInputStream GetClientInputStream()
		{
			return new j4o.io.FileInputStream(i_file);
		}

		public virtual j4o.io.FileOutputStream GetClientOutputStream()
		{
			return new j4o.io.FileOutputStream(i_file);
		}

		public virtual string GetFileName()
		{
			return fileName;
		}

		public virtual int GetLength()
		{
			return i_length;
		}

		public virtual double GetStatus()
		{
			if (i_status == com.db4o.ext.Status.PROCESSING && i_getStatusFrom != null)
			{
				return i_getStatusFrom.GetStatus();
			}
			if (i_status == com.db4o.ext.Status.UNUSED)
			{
				if (i_length > 0)
				{
					i_status = com.db4o.ext.Status.AVAILABLE;
				}
			}
			return i_status;
		}

		public virtual void GetStatusFrom(com.db4o.BlobStatus from)
		{
			i_getStatusFrom = from;
		}

		public virtual bool HasClassIndex()
		{
			return false;
		}

		public virtual void ReadFrom(j4o.io.File file)
		{
			if (!file.Exists())
			{
				throw new System.IO.IOException(com.db4o.Messages.Get(41, file.GetAbsolutePath())
					);
			}
			i_length = (int)file.Length();
			CheckExt(file);
			if (i_stream.IsClient())
			{
				i_file = file;
				((com.db4o.BlobTransport)i_stream).ReadBlobFrom(i_trans, this, file);
			}
			else
			{
				ReadLocal(file);
			}
		}

		public virtual void ReadLocal(j4o.io.File file)
		{
			bool copied = false;
			if (fileName == null)
			{
				j4o.io.File newFile = new j4o.io.File(ServerPath(), file.GetName());
				if (!newFile.Exists())
				{
					Copy(file, newFile);
					copied = true;
					fileName = newFile.GetName();
				}
			}
			if (!copied)
			{
				Copy(file, ServerFile(CheckExt(file), true));
			}
			lock (i_stream.i_lock)
			{
				i_stream.SetInternal(i_trans, this, false);
			}
			i_status = com.db4o.ext.Status.COMPLETED;
		}

		public virtual void PreDeactivate()
		{
		}

		public virtual j4o.io.File ServerFile(string promptName, bool writeToServer)
		{
			lock (i_stream.i_lock)
			{
				i_stream.Activate1(i_trans, this, 2);
			}
			string path = ServerPath();
			i_stream.ConfigImpl().EnsureDirExists(path);
			if (writeToServer)
			{
				if (fileName == null)
				{
					if (promptName != null)
					{
						fileName = promptName;
					}
					else
					{
						fileName = "b_" + j4o.lang.JavaSystem.CurrentTimeMillis();
					}
					string tryPath = fileName + i_ext;
					int i = 0;
					while (new j4o.io.File(path, tryPath).Exists())
					{
						tryPath = fileName + "_" + i++ + i_ext;
						if (i == 99)
						{
							i_status = com.db4o.ext.Status.ERROR;
							throw new System.IO.IOException(com.db4o.Messages.Get(40));
						}
					}
					fileName = tryPath;
					lock (i_stream.i_lock)
					{
						i_stream.SetInternal(i_trans, this, false);
					}
				}
			}
			else
			{
				if (fileName == null)
				{
					throw new System.IO.IOException(com.db4o.Messages.Get(38));
				}
			}
			string lastTryPath = path + j4o.io.File.separator + fileName;
			if (!writeToServer)
			{
				if (!(new j4o.io.File(lastTryPath).Exists()))
				{
					throw new System.IO.IOException(com.db4o.Messages.Get(39));
				}
			}
			return new j4o.io.File(lastTryPath);
		}

		private string ServerPath()
		{
			string path = i_stream.ConfigImpl().BlobPath();
			if (path == null)
			{
				path = "blobs";
			}
			i_stream.ConfigImpl().EnsureDirExists(path);
			return path;
		}

		public virtual void SetStatus(double status)
		{
			i_status = status;
		}

		public virtual void SetTrans(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
			i_stream = a_trans.Stream();
		}

		public virtual void WriteLocal(j4o.io.File file)
		{
			Copy(ServerFile(null, false), file);
			i_status = com.db4o.ext.Status.COMPLETED;
		}

		public virtual void WriteTo(j4o.io.File file)
		{
			if (GetStatus() == com.db4o.ext.Status.UNUSED)
			{
				throw new System.IO.IOException(com.db4o.Messages.Get(43));
			}
			if (i_stream.IsClient())
			{
				i_file = file;
				i_status = com.db4o.ext.Status.QUEUED;
				((com.db4o.BlobTransport)i_stream).WriteBlobTo(i_trans, this, file);
			}
			else
			{
				WriteLocal(file);
			}
		}

		public virtual void ReplicateFrom(object obj)
		{
		}

		public virtual object StoredTo(com.db4o.Transaction a_trans)
		{
			return this;
		}

		public virtual void SetYapObject(com.db4o.YapObject a_yapObject)
		{
		}
	}
}
