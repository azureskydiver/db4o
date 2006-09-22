namespace com.db4o
{
	internal abstract class MsgBlob : com.db4o.MsgD
	{
		internal com.db4o.BlobImpl _blob;

		internal int _currentByte;

		internal int _length;

		internal virtual double GetStatus()
		{
			if (_length != 0)
			{
				return (double)_currentByte / (double)_length;
			}
			return com.db4o.ext.Status.ERROR;
		}

		internal abstract void ProcessClient(com.db4o.foundation.network.YapSocket sock);

		internal virtual com.db4o.BlobImpl ServerGetBlobImpl()
		{
			com.db4o.BlobImpl blobImpl = null;
			int id = _payLoad.ReadInt();
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				blobImpl = (com.db4o.BlobImpl)stream.GetByID1(GetTransaction(), id);
				stream.Activate1(GetTransaction(), blobImpl, 3);
			}
			return blobImpl;
		}

		protected virtual void Copy(com.db4o.foundation.network.YapSocket sock, j4o.io.OutputStream
			 rawout, int length, bool update)
		{
			j4o.io.BufferedOutputStream @out = new j4o.io.BufferedOutputStream(rawout);
			byte[] buffer = new byte[com.db4o.BlobImpl.COPYBUFFER_LENGTH];
			int totalread = 0;
			while (totalread < length)
			{
				int stilltoread = length - totalread;
				int readsize = (stilltoread < buffer.Length ? stilltoread : buffer.Length);
				int curread = sock.Read(buffer, 0, readsize);
				if (curread < 0)
				{
					throw new System.IO.IOException();
				}
				@out.Write(buffer, 0, curread);
				totalread += curread;
				if (update)
				{
					_currentByte += curread;
				}
			}
			@out.Flush();
			@out.Close();
		}

		protected virtual void Copy(j4o.io.InputStream rawin, com.db4o.foundation.network.YapSocket
			 sock, bool update)
		{
			j4o.io.BufferedInputStream @in = new j4o.io.BufferedInputStream(rawin);
			byte[] buffer = new byte[com.db4o.BlobImpl.COPYBUFFER_LENGTH];
			int bytesread = -1;
			while ((bytesread = rawin.Read(buffer)) >= 0)
			{
				sock.Write(buffer, 0, bytesread);
				if (update)
				{
					_currentByte += bytesread;
				}
			}
			@in.Close();
		}
	}
}
