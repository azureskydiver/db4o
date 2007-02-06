namespace com.db4o.@internal.cs
{
	internal sealed class ClientTransaction : com.db4o.@internal.Transaction
	{
		private readonly com.db4o.@internal.cs.ClientObjectContainer i_client;

		private com.db4o.foundation.Tree i_yapObjectsToGc;

		internal ClientTransaction(com.db4o.@internal.cs.ClientObjectContainer a_stream, 
			com.db4o.@internal.Transaction a_parent) : base(a_stream, a_parent)
		{
			i_client = a_stream;
		}

		public override void Commit()
		{
			CommitTransactionListeners();
			ClearAll();
			i_client.WriteMsg(com.db4o.@internal.cs.messages.Msg.COMMIT, true);
		}

		protected override void ClearAll()
		{
			RemoveYapObjectReferences();
			base.ClearAll();
		}

		private void RemoveYapObjectReferences()
		{
			if (i_yapObjectsToGc != null)
			{
				i_yapObjectsToGc.Traverse(new _AnonymousInnerClass33(this));
			}
			i_yapObjectsToGc = null;
		}

		private sealed class _AnonymousInnerClass33 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass33(ClientTransaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				com.db4o.@internal.ObjectReference yo = (com.db4o.@internal.ObjectReference)((com.db4o.@internal.TreeIntObject
					)a_object)._object;
				this._enclosing.Stream().RemoveReference(yo);
			}

			private readonly ClientTransaction _enclosing;
		}

		public override bool Delete(com.db4o.@internal.ObjectReference @ref, int id, int 
			cascade)
		{
			if (!base.Delete(@ref, id, cascade))
			{
				return false;
			}
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.TA_DELETE
				.GetWriterForInts(this, new int[] { id, cascade });
			i_client.WriteMsg(msg, false);
			return true;
		}

		public override bool IsDeleted(int a_id)
		{
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.TA_IS_DELETED
				.GetWriterForInt(this, a_id);
			i_client.WriteMsg(msg, true);
			int res = i_client.ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg.TA_IS_DELETED
				).ReadInt();
			return res == 1;
		}

		public override object[] ObjectAndYapObjectBySignature(long a_uuid, byte[] a_signature
			)
		{
			int messageLength = com.db4o.@internal.Const4.LONG_LENGTH + com.db4o.@internal.Const4
				.INT_LENGTH + a_signature.Length;
			com.db4o.@internal.cs.messages.MsgD message = com.db4o.@internal.cs.messages.Msg.
				OBJECT_BY_UUID.GetWriterForLength(this, messageLength);
			message.WriteLong(a_uuid);
			message.WriteBytes(a_signature);
			i_client.WriteMsg(message);
			message = (com.db4o.@internal.cs.messages.MsgD)i_client.ExpectedResponse(com.db4o.@internal.cs.messages.Msg
				.OBJECT_BY_UUID);
			int id = message.ReadInt();
			if (id > 0)
			{
				return Stream().GetObjectAndYapObjectByID(this, id);
			}
			return new object[2];
		}

		public override void ProcessDeletes()
		{
			if (i_delete != null)
			{
				i_delete.Traverse(new _AnonymousInnerClass82(this));
			}
			i_delete = null;
			i_writtenUpdateDeletedMembers = null;
			i_client.WriteMsg(com.db4o.@internal.cs.messages.Msg.PROCESS_DELETES, false);
		}

		private sealed class _AnonymousInnerClass82 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass82(ClientTransaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				com.db4o.@internal.DeleteInfo info = (com.db4o.@internal.DeleteInfo)a_object;
				if (info._reference != null)
				{
					this._enclosing.i_yapObjectsToGc = com.db4o.foundation.Tree.Add(this._enclosing.i_yapObjectsToGc
						, new com.db4o.@internal.TreeIntObject(info._key, info._reference));
				}
			}

			private readonly ClientTransaction _enclosing;
		}

		public override void Rollback()
		{
			i_yapObjectsToGc = null;
			RollBackTransactionListeners();
			ClearAll();
		}

		public override void WriteUpdateDeleteMembers(int a_id, com.db4o.@internal.ClassMetadata
			 a_yc, int a_type, int a_cascade)
		{
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.WRITE_UPDATE_DELETE_MEMBERS
				.GetWriterForInts(this, new int[] { a_id, a_yc.GetID(), a_type, a_cascade });
			i_client.WriteMsg(msg, false);
		}

		public override void SetPointer(int a_id, int a_address, int a_length)
		{
		}
	}
}
