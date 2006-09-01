namespace com.db4o.inside.classindex
{
	/// <summary>client class index.</summary>
	/// <remarks>
	/// client class index. Largly intended to do nothing or
	/// redirect functionality to the server.
	/// </remarks>
	internal sealed class ClassIndexClient : com.db4o.inside.classindex.ClassIndex
	{
		internal ClassIndexClient(com.db4o.YapClass aYapClass) : base(aYapClass)
		{
		}

		public override void Add(int a_id)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		internal void EnsureActive()
		{
		}

		public override void Read(com.db4o.Transaction a_trans)
		{
		}

		internal override void SetDirty(com.db4o.YapStream a_stream)
		{
		}

		internal void Write(com.db4o.YapStream a_stream)
		{
		}

		public sealed override void WriteOwnID(com.db4o.Transaction trans, com.db4o.YapReader
			 a_writer)
		{
			a_writer.WriteInt(0);
		}
	}
}
