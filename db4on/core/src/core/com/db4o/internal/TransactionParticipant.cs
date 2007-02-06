namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public interface TransactionParticipant
	{
		void Commit(com.db4o.@internal.Transaction transaction);

		void Rollback(com.db4o.@internal.Transaction transaction);

		void Dispose(com.db4o.@internal.Transaction transaction);
	}
}
