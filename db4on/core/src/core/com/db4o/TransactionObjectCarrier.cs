namespace com.db4o
{
	/// <summary>TODO: Check if all time-consuming stuff is overridden!</summary>
	internal class TransactionObjectCarrier : com.db4o.Transaction
	{
		internal TransactionObjectCarrier(com.db4o.YapStream a_stream, com.db4o.Transaction
			 a_parent) : base(a_stream, a_parent)
		{
		}

		internal override void commit()
		{
		}

		internal override void slotFreeOnCommit(int a_id, int a_address, int a_length)
		{
		}

		internal override void slotFreeOnRollback(int a_id, int a_address, int a_length)
		{
		}

		internal override void slotFreeOnRollbackSetPointer(int a_id, int a_address, int 
			a_length)
		{
			setPointer(a_id, a_address, a_length);
		}

		internal override void slotFreeOnRollbackCommitSetPointer(int a_id, int newAddress
			, int newLength)
		{
			setPointer(a_id, newAddress, newLength);
		}

		internal override void slotFreePointerOnCommit(int a_id, int a_address, int a_length
			)
		{
		}

		internal override void setPointer(int a_id, int a_address, int a_length)
		{
			writePointer(a_id, a_address, a_length);
		}

		internal override bool supportsVirtualFields()
		{
			return false;
		}
	}
}
