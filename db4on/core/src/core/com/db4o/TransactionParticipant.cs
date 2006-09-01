namespace com.db4o
{
	/// <exclude></exclude>
	public interface TransactionParticipant
	{
		void Commit(com.db4o.Transaction transaction);

		void Rollback(com.db4o.Transaction transaction);

		void Dispose(com.db4o.Transaction transaction);
	}
}
