
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
		internal const int COPYBUFFER_LENGTH = 4096;

		public string fileName;

		public string i_ext;

		[com.db4o.Transient]
		private j4o.io.File i_file;

		[com.db4o.Transient]
		private com.db4o.MsgBlob i_getStatusFrom;

		public int i_length;

		[com.db4o.Transient]
		private double i_status = com.db4o.ext.Status.UNUSED;

		[com.db4o.Transient]
		private com.db4o.YapStream i_stream;

		[com.db4o.Transient]
		private com.db4o.Transaction i_trans;

		public virtual int adjustReadDepth(int a_depth)
		{
			return 1;
		}

		public virtual bool canBind()
		{
			return true;
		}

		private string checkExt(j4o.io.File file)
		{
			string name = file.getName();
			int pos = name.LastIndexOf(".");
			if (pos > 0)
			{
				i_ext = name.Substring(pos);
				return name.Substring(0, pos);
			}
			else
			{
				i_ext = "";
				return name;
			}
		}

		private void copy(j4o.io.File from, j4o.io.File to)
		{
			to.delete();
			j4o.io.BufferedInputStream _in = new j4o.io.BufferedInputStream(new j4o.io.FileInputStream
				(from));
			j4o.io.BufferedOutputStream _out = new j4o.io.BufferedOutputStream(new j4o.io.FileOutputStream
				(to));
			byte[] buffer = new byte[COPYBUFFER_LENGTH];
			int bytesread = -1;
			while ((bytesread = _in.read(buffer)) >= 0)
			{
				_out.write(buffer, 0, bytesread);
			}
			_out.flush();
			_out.close();
			_in.close();
		}

		public virtual object createDefault(com.db4o.Transaction a_trans)
		{
			com.db4o.BlobImpl bi = null;
			try
			{
				bi = (com.db4o.BlobImpl)j4o.lang.JavaSystem.clone(this);
				bi.setTrans(a_trans);
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
				return null;
			}
			return bi;
		}

		internal virtual j4o.io.FileInputStream getClientInputStream()
		{
			return new j4o.io.FileInputStream(i_file);
		}

		internal virtual j4o.io.FileOutputStream getClientOutputStream()
		{
			return new j4o.io.FileOutputStream(i_file);
		}

		public virtual string getFileName()
		{
			return fileName;
		}

		internal virtual int getLength()
		{
			return i_length;
		}

		public virtual double getStatus()
		{
			if (i_status == com.db4o.ext.Status.PROCESSING && i_getStatusFrom != null)
			{
				return i_getStatusFrom.getStatus();
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

		internal virtual void getStatusFrom(com.db4o.MsgBlob from)
		{
			i_getStatusFrom = from;
		}

		public virtual bool hasClassIndex()
		{
			return false;
		}

		public virtual void readFrom(j4o.io.File file)
		{
			if (!file.exists())
			{
				throw new System.IO.IOException(com.db4o.Messages.get(41, file.getAbsolutePath())
					);
			}
			i_length = (int)file.length();
			checkExt(file);
			if (i_stream.isClient())
			{
				i_file = file;
				com.db4o.MsgBlob msg = null;
				lock (i_stream.Lock())
				{
					i_stream.set(this);
					int id = (int)i_stream.getID(this);
					msg = (com.db4o.MsgBlob)com.db4o.Msg.WRITE_BLOB.getWriterForInt(i_trans, id);
					msg.i_blob = this;
					i_status = com.db4o.ext.Status.QUEUED;
				}
				((com.db4o.YapClient)i_stream).processBlobMessage(msg);
			}
			else
			{
				readLocal(file);
			}
		}

		public virtual void readLocal(j4o.io.File file)
		{
			bool copied = false;
			if (fileName == null)
			{
				j4o.io.File newFile = new j4o.io.File(serverPath(), file.getName());
				if (!newFile.exists())
				{
					copy(file, newFile);
					copied = true;
					fileName = newFile.getName();
				}
			}
			if (!copied)
			{
				copy(file, serverFile(checkExt(file), true));
			}
			lock (i_stream.i_lock)
			{
				i_stream.setInternal(i_trans, this, false);
			}
			i_status = com.db4o.ext.Status.COMPLETED;
		}

		public virtual void preDeactivate()
		{
		}

		internal virtual j4o.io.File serverFile(string promptName, bool writeToServer)
		{
			lock (i_stream.i_lock)
			{
				i_stream.activate1(i_trans, this, 2);
			}
			string path = serverPath();
			i_stream.i_config.ensureDirExists(path);
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
						fileName = "b_" + j4o.lang.JavaSystem.currentTimeMillis();
					}
					string tryPath = fileName + i_ext;
					int i = 0;
					while (new j4o.io.File(path, tryPath).exists())
					{
						tryPath = fileName + "_" + i++ + i_ext;
						if (i == 99)
						{
							i_status = com.db4o.ext.Status.ERROR;
							throw new System.IO.IOException(com.db4o.Messages.get(40));
						}
					}
					fileName = tryPath;
					lock (i_stream.i_lock)
					{
						i_stream.setInternal(i_trans, this, false);
					}
				}
			}
			else
			{
				if (fileName == null)
				{
					throw new System.IO.IOException(com.db4o.Messages.get(38));
				}
			}
			string lastTryPath = path + j4o.io.File.separator + fileName;
			if (!writeToServer)
			{
				if (!(new j4o.io.File(lastTryPath).exists()))
				{
					throw new System.IO.IOException(com.db4o.Messages.get(39));
				}
			}
			return new j4o.io.File(lastTryPath);
		}

		private string serverPath()
		{
			string path = i_stream.i_config.i_blobPath;
			if (path == null)
			{
				path = "blobs";
			}
			i_stream.i_config.ensureDirExists(path);
			return path;
		}

		internal virtual void setStatus(double status)
		{
			i_status = status;
		}

		public virtual void setTrans(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
			i_stream = a_trans.i_stream;
		}

		public virtual void writeLocal(j4o.io.File file)
		{
			copy(serverFile(null, false), file);
			i_status = com.db4o.ext.Status.COMPLETED;
		}

		public virtual void writeTo(j4o.io.File file)
		{
			if (getStatus() == com.db4o.ext.Status.UNUSED)
			{
				throw new System.IO.IOException(com.db4o.Messages.get(43));
			}
			if (i_stream.isClient())
			{
				i_file = file;
				com.db4o.MsgBlob msg = (com.db4o.MsgBlob)com.db4o.Msg.READ_BLOB.getWriterForInt(i_trans
					, (int)i_stream.getID(this));
				msg.i_blob = this;
				i_status = com.db4o.ext.Status.QUEUED;
				((com.db4o.YapClient)i_stream).processBlobMessage(msg);
			}
			else
			{
				writeLocal(file);
			}
		}

		public virtual void replicateFrom(object obj)
		{
		}

		public virtual object storedTo(com.db4o.Transaction a_trans)
		{
			return this;
		}

		public virtual void setYapObject(com.db4o.YapObject a_yapObject)
		{
		}
	}
}
