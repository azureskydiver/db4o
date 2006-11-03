namespace com.db4o.cs.messages
{
	public class MWriteBlob : com.db4o.cs.messages.MsgBlob
	{
		public override void ProcessClient(com.db4o.foundation.network.YapSocket sock)
		{
			com.db4o.cs.messages.Msg message = com.db4o.cs.messages.Msg.ReadMessage(GetTransaction
				(), sock);
			if (message.Equals(com.db4o.cs.messages.Msg.OK))
			{
				try
				{
					_currentByte = 0;
					_length = this._blob.GetLength();
					_blob.GetStatusFrom(this);
					_blob.SetStatus(com.db4o.ext.Status.PROCESSING);
					j4o.io.FileInputStream inBlob = this._blob.GetClientInputStream();
					Copy(inBlob, sock, true);
					sock.Flush();
					com.db4o.YapStream stream = GetStream();
					message = com.db4o.cs.messages.Msg.ReadMessage(GetTransaction(), sock);
					if (message.Equals(com.db4o.cs.messages.Msg.OK))
					{
						stream.Deactivate(_blob, int.MaxValue);
						stream.Activate(_blob, int.MaxValue);
						this._blob.SetStatus(com.db4o.ext.Status.COMPLETED);
					}
					else
					{
						this._blob.SetStatus(com.db4o.ext.Status.ERROR);
					}
				}
				catch (System.Exception e)
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
				}
			}
		}

		public override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			try
			{
				com.db4o.YapStream stream = GetStream();
				com.db4o.BlobImpl blobImpl = this.ServerGetBlobImpl();
				if (blobImpl != null)
				{
					blobImpl.SetTrans(GetTransaction());
					j4o.io.File file = blobImpl.ServerFile(null, true);
					com.db4o.cs.messages.Msg.OK.Write(stream, sock);
					j4o.io.FileOutputStream fout = new j4o.io.FileOutputStream(file);
					Copy(sock, fout, blobImpl.GetLength(), false);
					com.db4o.cs.messages.Msg.OK.Write(stream, sock);
				}
			}
			catch
			{
			}
			return true;
		}
	}
}
