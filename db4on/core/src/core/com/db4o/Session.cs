namespace com.db4o
{
	internal sealed class Session
	{
		internal readonly string i_fileName;

		internal com.db4o.YapStream i_stream;

		private int i_openCount;

		internal Session(string a_fileName)
		{
			i_fileName = a_fileName;
		}

		internal static void checkHackedVersion()
		{
		}

		/// <summary>returns true, if session is to be closed completely</summary>
		internal bool closeInstance()
		{
			i_openCount--;
			return i_openCount < 0;
		}

		public override bool Equals(object a_object)
		{
			return i_fileName.Equals(((com.db4o.Session)a_object).i_fileName);
		}

		internal string fileName()
		{
			return i_fileName;
		}

		internal com.db4o.YapStream subSequentOpen()
		{
			if (i_stream.isClosed())
			{
				return null;
			}
			i_openCount++;
			return i_stream;
		}
	}
}
