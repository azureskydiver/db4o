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
	/// <summary>Messages with Data for Client/Server Communication</summary>
	internal class MsgD : com.db4o.Msg
	{
		internal com.db4o.YapWriter payLoad;

		internal MsgD() : base()
		{
		}

		internal MsgD(string aName) : base(aName)
		{
		}

		internal override void fakePayLoad(com.db4o.Transaction a_trans)
		{
		}

		internal override com.db4o.YapWriter getByteLoad()
		{
			return payLoad;
		}

		internal sealed override com.db4o.YapWriter getPayLoad()
		{
			return payLoad;
		}

		internal com.db4o.MsgD getWriterForLength(com.db4o.Transaction a_trans, int length
			)
		{
			com.db4o.MsgD message = (com.db4o.MsgD)clone(a_trans);
			message.payLoad = new com.db4o.YapWriter(a_trans, length + com.db4o.YapConst.MESSAGE_LENGTH
				);
			message.writeInt(i_msgID);
			message.writeInt(length);
			if (a_trans.i_parentTransaction == null)
			{
				message.payLoad.append(com.db4o.YapConst.SYSTEM_TRANS);
			}
			else
			{
				message.payLoad.append(com.db4o.YapConst.USER_TRANS);
			}
			return message;
		}

		internal com.db4o.MsgD getWriter(com.db4o.Transaction a_trans)
		{
			return getWriterForLength(a_trans, 0);
		}

		internal com.db4o.MsgD getWriterFor2Ints(com.db4o.Transaction a_trans, int id, int
			 anInt)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPINT_LENGTH
				 * 2);
			message.writeInt(id);
			message.writeInt(anInt);
			return message;
		}

		internal com.db4o.MsgD getWriterFor3Ints(com.db4o.Transaction a_trans, int int1, 
			int int2, int int3)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPINT_LENGTH
				 * 3);
			message.writeInt(int1);
			message.writeInt(int2);
			message.writeInt(int3);
			return message;
		}

		internal com.db4o.MsgD getWriterFor4Ints(com.db4o.Transaction a_trans, int int1, 
			int int2, int int3, int int4)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPINT_LENGTH
				 * 4);
			message.writeInt(int1);
			message.writeInt(int2);
			message.writeInt(int3);
			message.writeInt(int4);
			return message;
		}

		internal com.db4o.MsgD getWriterForIntArray(com.db4o.Transaction a_trans, int[] ints
			, int length)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPINT_LENGTH
				 * (length + 1));
			message.writeInt(length);
			for (int i = 0; i < length; i++)
			{
				message.writeInt(ints[i]);
			}
			return message;
		}

		internal com.db4o.MsgD getWriterForInt(com.db4o.Transaction a_trans, int id)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPINT_LENGTH
				);
			message.writeInt(id);
			return message;
		}

		internal com.db4o.MsgD getWriterForIntString(com.db4o.Transaction a_trans, int anInt
			, string str)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.stringIO.length
				(str) + com.db4o.YapConst.YAPINT_LENGTH * 2);
			message.writeInt(anInt);
			message.writeString(str);
			return message;
		}

		internal com.db4o.MsgD getWriterForLong(com.db4o.Transaction a_trans, long a_long
			)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPLONG_LENGTH
				);
			message.writeLong(a_long);
			return message;
		}

		internal com.db4o.MsgD getWriterForString(com.db4o.Transaction a_trans, string str
			)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.stringIO.length
				(str) + com.db4o.YapConst.YAPINT_LENGTH);
			message.writeString(str);
			return message;
		}

		internal virtual com.db4o.MsgD getWriter(com.db4o.YapWriter bytes)
		{
			com.db4o.MsgD message = getWriterForLength(bytes.getTransaction(), bytes.getLength
				());
			message.payLoad.append(bytes._buffer);
			return message;
		}

		internal virtual byte[] readBytes()
		{
			return payLoad.readBytes(readInt());
		}

		internal int readInt()
		{
			return payLoad.readInt();
		}

		internal long readLong()
		{
			return com.db4o.YLong.readLong(payLoad);
		}

		internal sealed override com.db4o.Msg readPayLoad(com.db4o.Transaction a_trans, com.db4o.YapSocket
			 sock, com.db4o.YapWriter reader)
		{
			int length = reader.readInt();
			if ((reader.readByte() == com.db4o.YapConst.SYSTEM_TRANS) && (a_trans.i_parentTransaction
				 != null))
			{
				a_trans = a_trans.i_parentTransaction;
			}
			com.db4o.MsgD command = (com.db4o.MsgD)clone(a_trans);
			command.payLoad = new com.db4o.YapWriter(a_trans, length);
			command.payLoad.read(sock);
			return command;
		}

		internal string readString()
		{
			int length = readInt();
			return com.db4o.YapConst.stringIO.read(payLoad, length);
		}

		internal void writeBytes(byte[] aBytes)
		{
			writeInt(aBytes.Length);
			payLoad.append(aBytes);
		}

		internal void writeInt(int aInt)
		{
			payLoad.writeInt(aInt);
		}

		internal void writeLong(long aLong)
		{
			com.db4o.YLong.writeLong(aLong, payLoad);
		}

		internal void writeString(string aStr)
		{
			payLoad.writeInt(j4o.lang.JavaSystem.getLengthOf(aStr));
			com.db4o.YapConst.stringIO.write(payLoad, aStr);
		}
	}
}
