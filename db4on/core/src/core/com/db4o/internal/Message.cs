namespace com.db4o.@internal
{
	internal sealed class Message
	{
		internal readonly System.IO.TextWriter stream;

		internal Message(com.db4o.@internal.ObjectContainerBase a_stream, string msg)
		{
			stream = a_stream.ConfigImpl().OutStream();
			Print(msg, true);
		}

		internal Message(string a_StringParam, int a_intParam, System.IO.TextWriter a_stream
			, bool header)
		{
			stream = a_stream;
			Print(com.db4o.@internal.Messages.Get(a_intParam, a_StringParam), header);
		}

		internal Message(string a_StringParam, int a_intParam, System.IO.TextWriter a_stream
			) : this(a_StringParam, a_intParam, a_stream, true)
		{
		}

		private void Print(string msg, bool header)
		{
			if (stream != null)
			{
				if (header)
				{
					stream.WriteLine("[" + com.db4o.Db4o.Version() + "   " + com.db4o.@internal.handlers.DateHandler
						.Now() + "] ");
				}
				stream.WriteLine(" " + msg);
			}
		}
	}
}
