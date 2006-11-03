namespace com.db4o.cs
{
	internal class YapClientThread : j4o.lang.Thread
	{
		private com.db4o.cs.YapClient i_stream;

		private com.db4o.foundation.network.YapSocket i_socket;

		internal readonly com.db4o.foundation.Queue4 messageQueue;

		internal readonly com.db4o.foundation.Lock4 messageQueueLock;

		internal YapClientThread(com.db4o.cs.YapClient client, com.db4o.foundation.network.YapSocket
			 a_socket, com.db4o.foundation.Queue4 messageQueue_, com.db4o.foundation.Lock4 messageQueueLock_
			)
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
					com.db4o.cs.messages.Msg message;
					try
					{
						message = com.db4o.cs.messages.Msg.ReadMessage(i_stream.GetTransaction(), i_socket
							);
					}
					catch
					{
						messageQueueLock.Run(new _AnonymousInnerClass47(this));
						Close();
						return;
					}
					if (i_stream == null)
					{
						return;
					}
					if (com.db4o.cs.messages.Msg.PING.Equals(message))
					{
						i_stream.WriteMsg(com.db4o.cs.messages.Msg.OK);
					}
					else
					{
						if (com.db4o.cs.messages.Msg.CLOSE.Equals(message))
						{
							i_stream.LogMsg(35, i_stream.ToString());
							if (i_stream == null)
							{
								return;
							}
							i_stream = null;
							i_socket = null;
						}
						else
						{
							messageQueueLock.Run(new _AnonymousInnerClass83(this, message));
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
			public _AnonymousInnerClass47(YapClientThread _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object Run()
			{
				this._enclosing.messageQueue.Add(com.db4o.cs.messages.Msg.ERROR);
				this._enclosing.Close();
				this._enclosing.messageQueueLock.Awake();
				return null;
			}

			private readonly YapClientThread _enclosing;
		}

		private sealed class _AnonymousInnerClass83 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass83(YapClientThread _enclosing, com.db4o.cs.messages.Msg
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

			private readonly YapClientThread _enclosing;

			private readonly com.db4o.cs.messages.Msg message;
		}
	}
}
