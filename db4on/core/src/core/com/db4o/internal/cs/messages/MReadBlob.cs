namespace com.db4o.@internal.cs.messages
{
	public class MReadBlob : com.db4o.@internal.cs.messages.MsgBlob
	{
		public override void ProcessClient(com.db4o.foundation.network.Socket4 sock)
		{
			com.db4o.@internal.cs.messages.Msg message = com.db4o.@internal.cs.messages.Msg.ReadMessage
				(Transaction(), sock);
			if (message.Equals(com.db4o.@internal.cs.messages.Msg.LENGTH))
			{
				try
				{
					_currentByte = 0;
					_length = message.PayLoad().ReadInt();
					_blob.GetStatusFrom(this);
					_blob.SetStatus(com.db4o.ext.Status.PROCESSING);
					Copy(sock, this._blob.GetClientOutputStream(), _length, true);
					message = com.db4o.@internal.cs.messages.Msg.ReadMessage(Transaction(), sock);
					if (message.Equals(com.db4o.@internal.cs.messages.Msg.OK))
					{
						this._blob.SetStatus(com.db4o.ext.Status.COMPLETED);
					}
					else
					{
						this._blob.SetStatus(com.db4o.ext.Status.ERROR);
					}
				}
				catch
				{
				}
			}
			else
			{
				if (message.Equals(com.db4o.@internal.cs.messages.Msg.ERROR))
				{
					this._blob.SetStatus(com.db4o.ext.Status.ERROR);
				}
			}
		}

		public override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			try
			{
				com.db4o.@internal.BlobImpl blobImpl = this.ServerGetBlobImpl();
				if (blobImpl != null)
				{
					blobImpl.SetTrans(Transaction());
					j4o.io.File file = blobImpl.ServerFile(null, false);
					int length = (int)file.Length();
					com.db4o.foundation.network.Socket4 sock = serverThread.Socket();
					com.db4o.@internal.cs.messages.Msg.LENGTH.GetWriterForInt(Transaction(), length).
						Write(stream, sock);
					j4o.io.FileInputStream fin = new j4o.io.FileInputStream(file);
					Copy(fin, sock, false);
					sock.Flush();
					com.db4o.@internal.cs.messages.Msg.OK.Write(stream, sock);
				}
			}
			catch
			{
				serverThread.Write(com.db4o.@internal.cs.messages.Msg.ERROR);
			}
			return true;
		}
	}
}
