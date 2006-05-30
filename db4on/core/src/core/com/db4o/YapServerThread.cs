namespace com.db4o
{
	internal sealed class YapServerThread : j4o.lang.Thread
	{
		private string i_clientName;

		private bool i_loggedin;

		private long i_lastClientMessage;

		private readonly com.db4o.YapFile i_mainStream;

		private com.db4o.Transaction i_mainTrans;

		private int i_pingAttempts = 0;

		private int i_nullMessages;

		private bool i_rollbackOnClose = true;

		private bool i_sendCloseMessage = true;

		private readonly com.db4o.YapServer i_server;

		private com.db4o.foundation.network.YapSocket i_socket;

		private com.db4o.YapFile i_substituteStream;

		private com.db4o.Transaction i_substituteTrans;

		private com.db4o.Config4Impl i_config;

		internal readonly int i_threadID;

		internal YapServerThread(com.db4o.YapServer aServer, com.db4o.YapFile aStream, com.db4o.foundation.network.YapSocket
			 aSocket, int aThreadID, bool loggedIn)
		{
			i_loggedin = loggedIn;
			i_lastClientMessage = j4o.lang.JavaSystem.CurrentTimeMillis();
			i_server = aServer;
			i_config = (com.db4o.Config4Impl)i_server.Configure();
			i_mainStream = aStream;
			i_threadID = aThreadID;
			SetName("db4o message server " + aThreadID);
			i_mainTrans = new com.db4o.Transaction(aStream, aStream.GetSystemTransaction());
			try
			{
				i_socket = aSocket;
				i_socket.SetSoTimeout(((com.db4o.Config4Impl)aServer.Configure()).TimeoutServerSocket
					());
			}
			catch (System.Exception e)
			{
				i_socket.Close();
				throw (e);
			}
		}

		public void Close()
		{
			CloseSubstituteStream();
			try
			{
				if (i_sendCloseMessage)
				{
					com.db4o.Msg.CLOSE.Write(i_mainStream, i_socket);
				}
			}
			catch (System.Exception e)
			{
			}
			if (i_mainStream != null && i_mainTrans != null)
			{
				i_mainTrans.Close(i_rollbackOnClose);
			}
			try
			{
				i_socket.Close();
			}
			catch (System.Exception e)
			{
			}
			i_socket = null;
			try
			{
				i_server.RemoveThread(this);
			}
			catch (System.Exception e)
			{
			}
		}

		private void CloseSubstituteStream()
		{
			if (i_substituteStream != null)
			{
				if (i_substituteTrans != null)
				{
					i_substituteTrans.Close(i_rollbackOnClose);
					i_substituteTrans = null;
				}
				try
				{
					i_substituteStream.Close();
				}
				catch (System.Exception e)
				{
				}
				i_substituteStream = null;
			}
		}

		private com.db4o.YapFile GetStream()
		{
			if (i_substituteStream != null)
			{
				return i_substituteStream;
			}
			return i_mainStream;
		}

		internal com.db4o.Transaction GetTransaction()
		{
			if (i_substituteTrans != null)
			{
				return i_substituteTrans;
			}
			return i_mainTrans;
		}

		public override void Run()
		{
			while (i_socket != null)
			{
				try
				{
					if (!MessageProcessor())
					{
						break;
					}
				}
				catch (System.IO.IOException e)
				{
					break;
				}
				catch (System.Exception e)
				{
					if (i_mainStream == null || i_mainStream.IsClosed())
					{
						break;
					}
				}
				if (i_nullMessages > 20 || PingClientTimeoutReached())
				{
					if (i_pingAttempts > 5)
					{
						GetStream().LogMsg(33, i_clientName);
						break;
					}
					if (null == i_socket)
					{
						break;
					}
					com.db4o.Msg.PING.Write(GetStream(), i_socket);
					i_pingAttempts++;
				}
			}
			Close();
		}

		private bool PingClientTimeoutReached()
		{
			return (j4o.lang.JavaSystem.CurrentTimeMillis() - i_lastClientMessage > i_config.
				TimeoutPingClients());
		}

		private bool MessageProcessor()
		{
			com.db4o.Msg message = com.db4o.Msg.ReadMessage(GetTransaction(), i_socket);
			if (message == null)
			{
				i_nullMessages++;
				return true;
			}
			i_lastClientMessage = j4o.lang.JavaSystem.CurrentTimeMillis();
			i_nullMessages = 0;
			i_pingAttempts = 0;
			if (!i_loggedin)
			{
				if (com.db4o.Msg.LOGIN.Equals(message))
				{
					string userName = ((com.db4o.MsgD)message).ReadString();
					string password = ((com.db4o.MsgD)message).ReadString();
					com.db4o.User user = new com.db4o.User();
					user.name = userName;
					i_mainStream.ShowInternalClasses(true);
					com.db4o.User found = (com.db4o.User)i_mainStream.Get(user).Next();
					i_mainStream.ShowInternalClasses(false);
					if (found != null)
					{
						if (found.password.Equals(password))
						{
							i_clientName = userName;
							i_mainStream.LogMsg(32, i_clientName);
							com.db4o.Msg.LOGIN_OK.GetWriterForInt(GetTransaction(), i_mainStream.BlockSize())
								.Write(i_mainStream, i_socket);
							i_loggedin = true;
							SetName("db4o server socket for client " + i_clientName);
						}
						else
						{
							com.db4o.Msg.FAILED.Write(i_mainStream, i_socket);
							return false;
						}
					}
					else
					{
						com.db4o.Msg.FAILED.Write(i_mainStream, i_socket);
						return false;
					}
				}
				return true;
			}
			if (message.ProcessMessageAtServer(i_socket))
			{
				return true;
			}
			if (com.db4o.Msg.PING.Equals(message))
			{
				com.db4o.Msg.OK.Write(GetStream(), i_socket);
				return true;
			}
			if (com.db4o.Msg.CLOSE.Equals(message))
			{
				com.db4o.Msg.CLOSE.Write(GetStream(), i_socket);
				GetTransaction().Commit();
				i_sendCloseMessage = false;
				GetStream().LogMsg(34, i_clientName);
				return false;
			}
			if (com.db4o.Msg.IDENTITY.Equals(message))
			{
				RespondInt((int)GetStream().GetID(GetStream().BootRecord().i_db));
				return true;
			}
			if (com.db4o.Msg.CURRENT_VERSION.Equals(message))
			{
				com.db4o.YapStream stream = GetStream();
				long ver = 0;
				lock (stream)
				{
					ver = GetStream().BootRecord().CurrentVersion();
				}
				com.db4o.Msg.ID_LIST.GetWriterForLong(GetTransaction(), ver).Write(GetStream(), i_socket
					);
				return true;
			}
			if (com.db4o.Msg.RAISE_VERSION.Equals(message))
			{
				long minimumVersion = ((com.db4o.MsgD)message).ReadLong();
				com.db4o.YapStream stream = GetStream();
				lock (stream)
				{
					stream.RaiseVersion(minimumVersion);
				}
				return true;
			}
			if (com.db4o.Msg.GET_THREAD_ID.Equals(message))
			{
				RespondInt(i_threadID);
				return true;
			}
			if (com.db4o.Msg.SWITCH_TO_FILE.Equals(message))
			{
				SwitchToFile(message);
				return true;
			}
			if (com.db4o.Msg.SWITCH_TO_MAIN_FILE.Equals(message))
			{
				SwitchToMainFile();
				return true;
			}
			if (com.db4o.Msg.USE_TRANSACTION.Equals(message))
			{
				UseTransaction(message);
				return true;
			}
			return true;
		}

		private void SwitchToFile(com.db4o.Msg message)
		{
			lock (i_mainStream.i_lock)
			{
				string fileName = ((com.db4o.MsgD)message).ReadString();
				try
				{
					CloseSubstituteStream();
					i_substituteStream = (com.db4o.YapFile)com.db4o.Db4o.OpenFile(fileName);
					i_substituteTrans = new com.db4o.Transaction(i_substituteStream, i_substituteStream
						.GetSystemTransaction());
					i_substituteStream.i_config.SetMessageRecipient(i_mainStream.i_config.MessageRecipient
						());
					com.db4o.Msg.OK.Write(GetStream(), i_socket);
				}
				catch (System.Exception e)
				{
					CloseSubstituteStream();
					com.db4o.Msg.ERROR.Write(GetStream(), i_socket);
				}
			}
		}

		private void SwitchToMainFile()
		{
			lock (i_mainStream.i_lock)
			{
				CloseSubstituteStream();
				com.db4o.Msg.OK.Write(GetStream(), i_socket);
			}
		}

		private void UseTransaction(com.db4o.Msg message)
		{
			int threadID = ((com.db4o.MsgD)message).ReadInt();
			com.db4o.YapServerThread transactionThread = i_server.FindThread(threadID);
			if (transactionThread != null)
			{
				com.db4o.Transaction transToUse = transactionThread.GetTransaction();
				if (i_substituteTrans != null)
				{
					i_substituteTrans = transToUse;
				}
				else
				{
					i_mainTrans = transToUse;
				}
				i_rollbackOnClose = false;
			}
		}

		private void RespondInt(int response)
		{
			com.db4o.Msg.ID_LIST.GetWriterForInt(GetTransaction(), response).Write(GetStream(
				), i_socket);
		}
	}
}
