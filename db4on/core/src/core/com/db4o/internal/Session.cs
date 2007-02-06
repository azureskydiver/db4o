namespace com.db4o.@internal
{
	internal sealed class Session
	{
		internal readonly string i_fileName;

		internal com.db4o.@internal.ObjectContainerBase i_stream;

		private int i_openCount;

		internal Session(string a_fileName)
		{
			i_fileName = a_fileName;
		}

		/// <summary>returns true, if session is to be closed completely</summary>
		internal bool CloseInstance()
		{
			i_openCount--;
			return i_openCount < 0;
		}

		/// <summary>Will raise an exception if argument class doesn't match this class - violates equals() contract in favor of failing fast.
		/// 	</summary>
		/// <remarks>Will raise an exception if argument class doesn't match this class - violates equals() contract in favor of failing fast.
		/// 	</remarks>
		public override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (null == obj)
			{
				return false;
			}
			if (j4o.lang.JavaSystem.GetClassForObject(this) != j4o.lang.JavaSystem.GetClassForObject
				(obj))
			{
				com.db4o.@internal.Exceptions4.ShouldNeverHappen();
			}
			return i_fileName.Equals(((com.db4o.@internal.Session)obj).i_fileName);
		}

		public override int GetHashCode()
		{
			return i_fileName.GetHashCode();
		}

		internal string FileName()
		{
			return i_fileName;
		}

		internal com.db4o.@internal.ObjectContainerBase SubSequentOpen()
		{
			if (i_stream.IsClosed())
			{
				return null;
			}
			i_openCount++;
			return i_stream;
		}
	}
}
