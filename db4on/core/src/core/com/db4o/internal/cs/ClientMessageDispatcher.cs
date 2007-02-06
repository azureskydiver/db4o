namespace com.db4o.@internal.cs
{
	internal class ClientMessageDispatcher : j4o.lang.Thread
	{
		private com.db4o.@internal.cs.ClientObjectContainer i_stream;

		private com.db4o.foundation.network.Socket4 i_socket;

		internal readonly com.db4o.foundation.Queue4 messageQueue;

		internal readonly com.db4o.foundation.Lock4 messageQueueLock;

		internal ClientMessageDispatcher(com.db4o.@internal.cs.ClientObjectContainer client
			, com.db4o.foundation.network.Socket4 a_socket, com.db4o.foundation.Queue4 messageQueue_
			, com.db4o.foundation.Lock4 messageQueueLock_)
		{
			lock (this)
			{
				i_stream = client;
				messageQueue = messageQueue_;
				i_socket = a_socket;
				messageQueueLock = messageQueueLock_;
			}
		}

		internal virtual bool IsClosed()
		{
			lock (this)
			{
				return i_socket == null;
			}
		}

		internal virtual void Close()
		{
			lock (this)
			{
				i_stream = null;
				i_socket = null;
			}
		}

		public override void Run()
		{
			while (i_socket != null)
			{
				try
				{
					if (i_stream == null)
					{
						return;
					}
					com.db4o.@internal.cs.messages.Msg message;
					try
					{
						message = com.db4o.@internal.cs.messages.Msg.ReadMessage(i_stream.GetTransaction(
							), i_socket);
					}
					catch
					{
						messageQueueLock.Run(new _AnonymousInnerClass47(this));
						Close();
						return;
					}
					if (com.db4o.@internal.cs.messages.Msg.PING.Equals(message))
					{
						i_stream.WriteMsg(com.db4o.@internal.cs.messages.Msg.OK);
					}
					else
					{
						if (com.db4o.@internal.cs.messages.Msg.CLOSE.Equals(message))
						{
							i_stream.LogMsg(35, i_stream.ToString());
							i_stream = null;
							i_socket = null;
						}
						else
						{
							messageQueueLock.Run(new _AnonymousInnerClass77(this, message));
						}
					}
				}
				catch
				{
					Close();
					return;
				}
			}
		}

		private sealed class _AnonymousInnerClass47 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass47(ClientMessageDispatcher _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object Run()
			{
				this._enclosing.messageQueue.Add(com.db4o.@internal.cs.messages.Msg.ERROR);
				this._enclosing.Close();
				this._enclosing.messageQueueLock.Awake();
				return null;
			}

			private readonly ClientMessageDispatcher _enclosing;
		}

		private sealed class _AnonymousInnerClass77 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass77(ClientMessageDispatcher _enclosing, com.db4o.@internal.cs.messages.Msg
				 message)
			{
				this._enclosing = _enclosing;
				this.message = message;
			}

			public object Run()
			{
				this._enclosing.messageQueue.Add(message);
				this._enclosing.messageQueueLock.Awake();
				return null;
			}

			private readonly ClientMessageDispatcher _enclosing;

			private readonly com.db4o.@internal.cs.messages.Msg message;
		}
	}
}
