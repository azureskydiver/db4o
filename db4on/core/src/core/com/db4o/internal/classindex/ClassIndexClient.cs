namespace com.db4o.@internal.classindex
{
	/// <summary>client class index.</summary>
	/// <remarks>
	/// client class index. Largly intended to do nothing or
	/// redirect functionality to the server.
	/// </remarks>
	internal sealed class ClassIndexClient : com.db4o.@internal.classindex.ClassIndex
	{
		internal ClassIndexClient(com.db4o.@internal.ClassMetadata aYapClass) : base(aYapClass
			)
		{
		}

		public override void Add(int a_id)
		{
			throw com.db4o.@internal.Exceptions4.VirtualException();
		}

		internal void EnsureActive()
		{
		}

		public override void Read(com.db4o.@internal.Transaction a_trans)
		{
		}

		internal override void SetDirty(com.db4o.@internal.ObjectContainerBase a_stream)
		{
		}

		public sealed override void WriteOwnID(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 a_writer)
		{
			a_writer.WriteInt(0);
		}
	}
}
