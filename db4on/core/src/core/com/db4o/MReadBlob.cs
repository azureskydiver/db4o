namespace com.db4o
{
	internal class MReadBlob : com.db4o.MsgBlob
	{
		internal override void ProcessClient(com.db4o.foundation.network.YapSocket sock)
		{
			com.db4o.Msg message = com.db4o.Msg.ReadMessage(GetTransaction(), sock);
			if (message.Equals(com.db4o.Msg.LENGTH))
			{
				try
				{
					_currentByte = 0;
					_length = message.GetPayLoad().ReadInt();
					_blob.GetStatusFrom(this);
					_blob.SetStatus(com.db4o.ext.Status.PROCESSING);
					Copy(sock, this._blob.GetClientOutputStream(), _length, true);
					message = com.db4o.Msg.ReadMessage(GetTransaction(), sock);
					if (message.Equals(com.db4o.Msg.OK))
					{
						this._blob.SetStatus(com.db4o.ext.Status.COMPLETED);
					}
					else
					{
						this._blob.SetStatus(com.db4o.ext.Status.ERROR);
					}
				}
				catch (System.Exception e)
				{
				}
			}
			else
			{
				if (message.Equals(com.db4o.Msg.ERROR))
				{
					this._blob.SetStatus(com.db4o.ext.Status.ERROR);
				}
			}
		}

		internal override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			try
			{
				com.db4o.BlobImpl blobImpl = this.ServerGetBlobImpl();
				if (blobImpl != null)
				{
					blobImpl.SetTrans(GetTransaction());
					j4o.io.File file = blobImpl.ServerFile(null, false);
					int length = (int)file.Length();
					com.db4o.Msg.LENGTH.GetWriterForInt(GetTransaction(), length).Write(stream, sock);
					j4o.io.FileInputStream fin = new j4o.io.FileInputStream(file);
					Copy(fin, sock, false);
					sock.Flush();
					com.db4o.Msg.OK.Write(stream, sock);
				}
			}
			catch (System.Exception e)
			{
				com.db4o.Msg.ERROR.Write(stream, sock);
			}
			return true;
		}
	}
}
