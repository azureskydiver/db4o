
namespace com.db4o
{
	internal sealed class TransactionClient : com.db4o.Transaction
	{
		private readonly com.db4o.YapClient i_client;

		private com.db4o.Tree i_yapObjectsToGc;

		internal TransactionClient(com.db4o.YapClient a_stream, com.db4o.Transaction a_parent
			) : base(a_stream, a_parent)
		{
			i_client = a_stream;
		}

		internal override void beginEndSet()
		{
			if (i_delete != null)
			{
				i_delete.traverse(new _AnonymousInnerClass20(this));
			}
			i_delete = null;
			i_writtenUpdateDeletedMembers = null;
			i_client.writeMsg(com.db4o.Msg.TA_BEGIN_END_SET);
		}

		private sealed class _AnonymousInnerClass20 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass20(TransactionClient _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)a_object;
				if (info._delete && info._reference != null)
				{
					this._enclosing.i_yapObjectsToGc = com.db4o.Tree.add(this._enclosing.i_yapObjectsToGc
						, new com.db4o.TreeIntObject(info.i_key, info._reference));
				}
			}

			private readonly TransactionClient _enclosing;
		}

		internal override void commit()
		{
			commitTransactionListeners();
			if (i_yapObjectsToGc != null)
			{
				i_yapObjectsToGc.traverse(new _AnonymousInnerClass37(this));
			}
			i_yapObjectsToGc = null;
			i_client.writeMsg(com.db4o.Msg.COMMIT);
		}

		private sealed class _AnonymousInnerClass37 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass37(TransactionClient _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				com.db4o.YapObject yo = (com.db4o.YapObject)((com.db4o.TreeIntObject)a_object).i_object;
				this._enclosing.i_stream.yapObjectGCd(yo);
			}

			private readonly TransactionClient _enclosing;
		}

		internal override void delete(com.db4o.YapObject a_yo, int a_cascade)
		{
			base.delete(a_yo, a_cascade);
			i_client.writeMsg(com.db4o.Msg.TA_DELETE.getWriterForInts(this, new int[] { a_yo.
				getID(), a_cascade }));
		}

		internal override void dontDelete(int classID, int a_id)
		{
			base.dontDelete(classID, a_id);
			i_client.writeMsg(com.db4o.Msg.TA_DONT_DELETE.getWriterForInts(this, new int[] { 
				classID, a_id }));
		}

		internal override bool isDeleted(int a_id)
		{
			i_client.writeMsg(com.db4o.Msg.TA_IS_DELETED.getWriterForInt(this, a_id));
			int res = i_client.expectedByteResponse(com.db4o.Msg.TA_IS_DELETED).readInt();
			return res == 1;
		}

		internal override object[] objectAndYapObjectBySignature(long a_uuid, byte[] a_signature
			)
		{
			int messageLength = com.db4o.YapConst.YAPLONG_LENGTH + com.db4o.YapConst.YAPINT_LENGTH
				 + a_signature.Length;
			com.db4o.MsgD message = com.db4o.Msg.OBJECT_BY_UUID.getWriterForLength(this, messageLength
				);
			message.writeLong(a_uuid);
			message.writeBytes(a_signature);
			i_client.writeMsg(message);
			message = (com.db4o.MsgD)i_client.expectedResponse(com.db4o.Msg.OBJECT_BY_UUID);
			int id = message.readInt();
			if (id > 0)
			{
				return i_stream.getObjectAndYapObjectByID(this, id);
			}
			return new object[2];
		}

		public override void rollback()
		{
			i_yapObjectsToGc = null;
			rollBackTransactionListeners();
		}

		internal override void writeUpdateDeleteMembers(int a_id, com.db4o.YapClass a_yc, 
			int a_type, int a_cascade)
		{
			i_client.writeMsg(com.db4o.Msg.WRITE_UPDATE_DELETE_MEMBERS.getWriterForInts(this, 
				new int[] { a_id, a_yc.getID(), a_type, a_cascade }));
		}
	}
}
