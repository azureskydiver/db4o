namespace com.db4o
{
	internal sealed class Message
	{
		internal readonly j4o.io.PrintStream stream;

		internal Message(com.db4o.YapStream a_stream, string msg)
		{
			stream = a_stream.ConfigImpl().OutStream();
			Print(msg, true);
		}

		internal Message(string a_StringParam, int a_intParam, j4o.io.PrintStream a_stream
			, bool header)
		{
			stream = a_stream;
			Print(com.db4o.Messages.Get(a_intParam, a_StringParam), header);
		}

		internal Message(string a_StringParam, int a_intParam, j4o.io.PrintStream a_stream
			) : this(a_StringParam, a_intParam, a_stream, true)
		{
		}

		private void Print(string msg, bool header)
		{
			if (stream != null)
			{
				if (header)
				{
					stream.Println("[" + com.db4o.Db4o.Version() + "   " + com.db4o.YDate.Now() + "] "
						);
				}
				stream.Println(" " + msg);
			}
		}
	}
}
