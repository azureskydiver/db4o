/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
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
				i_delete.traverse(new _AnonymousInnerClass18(this));
			}
			i_delete = null;
			i_writtenUpdateDeletedMembers = null;
			i_client.writeMsg(com.db4o.Msg.TA_BEGIN_END_SET);
		}

		private sealed class _AnonymousInnerClass18 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass18(TransactionClient _enclosing)
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
				i_yapObjectsToGc.traverse(new _AnonymousInnerClass35(this));
			}
			i_yapObjectsToGc = null;
			i_client.writeMsg(com.db4o.Msg.COMMIT);
		}

		private sealed class _AnonymousInnerClass35 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass35(TransactionClient _enclosing)
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

		internal override void dontDelete(int a_id)
		{
			base.dontDelete(a_id);
			i_client.writeMsg(com.db4o.Msg.TA_DONT_DELETE.getWriterForInt(this, a_id));
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
