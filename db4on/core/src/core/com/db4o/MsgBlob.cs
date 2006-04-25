namespace com.db4o
{
	internal abstract class MsgBlob : com.db4o.MsgD
	{
		internal com.db4o.BlobImpl _blob;

		internal int _currentByte;

		internal int _length;

		internal virtual double getStatus()
		{
			if (_length != 0)
			{
				return (double)_currentByte / (double)_length;
			}
			return com.db4o.ext.Status.ERROR;
		}

		internal abstract void processClient(com.db4o.foundation.network.YapSocket sock);

		internal virtual com.db4o.BlobImpl serverGetBlobImpl()
		{
			com.db4o.BlobImpl blobImpl = null;
			int id = _payLoad.readInt();
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				blobImpl = (com.db4o.BlobImpl)stream.getByID1(getTransaction(), id);
				stream.activate1(getTransaction(), blobImpl, 3);
			}
			return blobImpl;
		}

		protected virtual void copy(com.db4o.foundation.network.YapSocket sock, j4o.io.OutputStream
			 rawout, int length, bool update)
		{
			j4o.io.BufferedOutputStream _out = new j4o.io.BufferedOutputStream(rawout);
			byte[] buffer = new byte[com.db4o.BlobImpl.COPYBUFFER_LENGTH];
			int totalread = 0;
			while (totalread < length)
			{
				int stilltoread = length - totalread;
				int readsize = (stilltoread < buffer.Length ? stilltoread : buffer.Length);
				int curread = sock.read(buffer, 0, readsize);
				if (curread < 0)
				{
					throw new System.IO.IOException();
				}
				_out.write(buffer, 0, curread);
				totalread += curread;
				if (update)
				{
					_currentByte += curread;
				}
			}
			_out.flush();
			_out.close();
		}

		protected virtual void copy(j4o.io.InputStream rawin, com.db4o.foundation.network.YapSocket
			 sock, bool update)
		{
			j4o.io.BufferedInputStream _in = new j4o.io.BufferedInputStream(rawin);
			byte[] buffer = new byte[com.db4o.BlobImpl.COPYBUFFER_LENGTH];
			int bytesread = -1;
			while ((bytesread = rawin.read(buffer)) >= 0)
			{
				sock.write(buffer, 0, bytesread);
				if (update)
				{
					_currentByte += bytesread;
				}
			}
			_in.close();
		}
	}
}
