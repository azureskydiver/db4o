namespace com.db4o
{
	/// <summary>TODO: Check if all time-consuming stuff is overridden!</summary>
	internal class TransactionObjectCarrier : com.db4o.Transaction
	{
		internal TransactionObjectCarrier(com.db4o.YapStream a_stream, com.db4o.Transaction
			 a_parent) : base(a_stream, a_parent)
		{
		}

		internal override void Commit()
		{
		}

		public override void SlotFreeOnCommit(int a_id, int a_address, int a_length)
		{
		}

		internal override void SlotFreeOnRollback(int a_id, int a_address, int a_length)
		{
		}

		internal override void SlotFreeOnRollbackSetPointer(int a_id, int a_address, int 
			a_length)
		{
			SetPointer(a_id, a_address, a_length);
		}

		internal override void SlotFreeOnRollbackCommitSetPointer(int a_id, int newAddress
			, int newLength)
		{
			SetPointer(a_id, newAddress, newLength);
		}

		internal override void SlotFreePointerOnCommit(int a_id, int a_address, int a_length
			)
		{
		}

		public override void SetPointer(int a_id, int a_address, int a_length)
		{
			WritePointer(a_id, a_address, a_length);
		}

		internal override bool SupportsVirtualFields()
		{
			return false;
		}
	}
}
