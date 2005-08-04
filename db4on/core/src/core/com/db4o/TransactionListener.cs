namespace com.db4o
{
	/// <summary>
	/// allows registration with a transaction to be notified of
	/// commit and rollback
	/// </summary>
	/// <exclude></exclude>
	public interface TransactionListener
	{
		void preCommit();

		void postRollback();
	}
}
