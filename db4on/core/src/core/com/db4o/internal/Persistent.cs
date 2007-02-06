namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public interface Persistent
	{
		/// <moveto>
		/// new com.db4o.internal.Persistent interface
		/// all four of the following abstract methods
		/// </moveto>
		byte GetIdentifier();

		int OwnLength();

		void ReadThis(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer reader
			);

		void WriteThis(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer writer
			);
	}
}
