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
	internal class MsgObject : com.db4o.MsgD
	{
		private const int LENGTH_FOR_ALL = com.db4o.YapConst.YAPID_LENGTH + (com.db4o.YapConst
			.YAPINT_LENGTH * 3);

		private const int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;

		internal int i_id;

		internal int i_address;

		internal virtual com.db4o.MsgD getWriter(com.db4o.YapWriter bytes, int[] prependInts
			)
		{
			int lengthNeeded = bytes.getLength() + LENGTH_FOR_FIRST;
			if (prependInts != null)
			{
				lengthNeeded += (prependInts.Length * com.db4o.YapConst.YAPINT_LENGTH);
			}
			int embeddedCount = bytes.embeddedCount();
			if (embeddedCount > 0)
			{
				lengthNeeded += (LENGTH_FOR_ALL * embeddedCount) + bytes.embeddedLength();
			}
			com.db4o.MsgD message = getWriterForLength(bytes.getTransaction(), lengthNeeded);
			if (prependInts != null)
			{
				for (int i = 0; i < prependInts.Length; i++)
				{
					message.payLoad.writeInt(prependInts[i]);
				}
			}
			message.payLoad.writeInt(embeddedCount);
			bytes.appendTo(message.payLoad, -1);
			return message;
		}

		internal override com.db4o.MsgD getWriter(com.db4o.YapWriter bytes)
		{
			return getWriter(bytes, null);
		}

		internal virtual com.db4o.MsgD getWriter(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 bytes)
		{
			return getWriter(bytes, new int[] { a_yapClass.getID() });
		}

		internal virtual com.db4o.MsgD getWriter(com.db4o.YapClass a_yapClass, int a_param
			, com.db4o.YapWriter bytes)
		{
			return getWriter(bytes, new int[] { a_yapClass.getID(), a_param });
		}

		public com.db4o.YapWriter unmarshall()
		{
			return unmarshall(0);
		}

		public com.db4o.YapWriter unmarshall(int addLengthBeforeFirst)
		{
			payLoad.setTransaction(getTransaction());
			int embeddedCount = payLoad.readInt();
			int length = payLoad.readInt();
			if (length == 0)
			{
				return null;
			}
			i_id = payLoad.readInt();
			i_address = payLoad.readInt();
			if (embeddedCount == 0)
			{
				payLoad.removeFirstBytes(LENGTH_FOR_FIRST + addLengthBeforeFirst);
			}
			else
			{
				payLoad._offset += length;
				com.db4o.YapWriter[] embedded = new com.db4o.YapWriter[embeddedCount + 1];
				embedded[0] = payLoad;
				new com.db4o.YapWriter(payLoad, embedded, 1);
				payLoad.trim4(LENGTH_FOR_FIRST + addLengthBeforeFirst, length);
			}
			payLoad.useSlot(i_id, i_address, length);
			return payLoad;
		}
	}
}
