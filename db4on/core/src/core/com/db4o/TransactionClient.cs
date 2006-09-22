namespace com.db4o
{
	internal sealed class TransactionClient : com.db4o.Transaction
	{
		private readonly com.db4o.YapClient i_client;

		protected com.db4o.foundation.Tree i_yapObjectsToGc;

		internal TransactionClient(com.db4o.YapClient a_stream, com.db4o.Transaction a_parent
			) : base(a_stream, a_parent)
		{
			i_client = a_stream;
		}

		internal override void BeginEndSet()
		{
			if (i_delete != null)
			{
				i_delete.Traverse(new _AnonymousInnerClass20(this));
			}
			i_delete = null;
			i_writtenUpdateDeletedMembers = null;
			i_client.WriteMsg(com.db4o.Msg.TA_BEGIN_END_SET);
		}

		private sealed class _AnonymousInnerClass20 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass20(TransactionClient _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)a_object;
				if (info._delete && info._reference != null)
				{
					this._enclosing.i_yapObjectsToGc = com.db4o.foundation.Tree.Add(this._enclosing.i_yapObjectsToGc
						, new com.db4o.TreeIntObject(info._key, info._reference));
				}
			}

			private readonly TransactionClient _enclosing;
		}

		public override void Commit()
		{
			CommitTransactionListeners();
			if (i_yapObjectsToGc != null)
			{
				i_yapObjectsToGc.Traverse(new _AnonymousInnerClass37(this));
			}
			i_yapObjectsToGc = null;
			i_client.WriteMsg(com.db4o.Msg.COMMIT);
		}

		private sealed class _AnonymousInnerClass37 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass37(TransactionClient _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				com.db4o.YapObject yo = (com.db4o.YapObject)((com.db4o.TreeIntObject)a_object)._object;
				this._enclosing.Stream().RemoveReference(yo);
			}

			private readonly TransactionClient _enclosing;
		}

		internal override void Delete(com.db4o.YapObject a_yo, int a_cascade)
		{
			base.Delete(a_yo, a_cascade);
			i_client.WriteMsg(com.db4o.Msg.TA_DELETE.GetWriterForInts(this, new int[] { a_yo.
				GetID(), a_cascade }));
		}

		internal override void DontDelete(int classID, int a_id)
		{
			base.DontDelete(classID, a_id);
			i_client.WriteMsg(com.db4o.Msg.TA_DONT_DELETE.GetWriterForInts(this, new int[] { 
				classID, a_id }));
		}

		internal override bool IsDeleted(int a_id)
		{
			i_client.WriteMsg(com.db4o.Msg.TA_IS_DELETED.GetWriterForInt(this, a_id));
			int res = i_client.ExpectedByteResponse(com.db4o.Msg.TA_IS_DELETED).ReadInt();
			return res == 1;
		}

		internal override object[] ObjectAndYapObjectBySignature(long a_uuid, byte[] a_signature
			)
		{
			int messageLength = com.db4o.YapConst.LONG_LENGTH + com.db4o.YapConst.INT_LENGTH 
				+ a_signature.Length;
			com.db4o.MsgD message = com.db4o.Msg.OBJECT_BY_UUID.GetWriterForLength(this, messageLength
				);
			message.WriteLong(a_uuid);
			message.WriteBytes(a_signature);
			i_client.WriteMsg(message);
			message = (com.db4o.MsgD)i_client.ExpectedResponse(com.db4o.Msg.OBJECT_BY_UUID);
			int id = message.ReadInt();
			if (id > 0)
			{
				return Stream().GetObjectAndYapObjectByID(this, id);
			}
			return new object[2];
		}

		public override void Rollback()
		{
			i_yapObjectsToGc = null;
			RollBackTransactionListeners();
		}

		internal override void WriteUpdateDeleteMembers(int a_id, com.db4o.YapClass a_yc, 
			int a_type, int a_cascade)
		{
			i_client.WriteMsg(com.db4o.Msg.WRITE_UPDATE_DELETE_MEMBERS.GetWriterForInts(this, 
				new int[] { a_id, a_yc.GetID(), a_type, a_cascade }));
		}
	}
}
