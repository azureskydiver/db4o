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
	internal sealed class MReadBytes : com.db4o.MsgD
	{
		internal sealed override com.db4o.YapWriter getByteLoad()
		{
			int address = this.payLoad.readInt();
			int length = this.payLoad.getLength() - (com.db4o.YapConst.YAPINT_LENGTH);
			this.payLoad.removeFirstBytes(com.db4o.YapConst.YAPINT_LENGTH);
			this.payLoad.useSlot(address, length);
			return this.payLoad;
		}

		internal sealed override com.db4o.MsgD getWriter(com.db4o.YapWriter bytes)
		{
			com.db4o.MsgD message = this.getWriterForLength(bytes.getTransaction(), bytes.getLength
				() + com.db4o.YapConst.YAPINT_LENGTH);
			message.payLoad.writeInt(bytes.getAddress());
			message.payLoad.append(bytes._buffer);
			return message;
		}

		internal sealed override bool processMessageAtServer(com.db4o.YapSocket sock)
		{
			com.db4o.YapStream stream = getStream();
			int address = this.readInt();
			int length = this.readInt();
			lock (stream.i_lock)
			{
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(this.getTransaction(), address, 
					length);
				try
				{
					stream.readBytes(bytes._buffer, address, length);
					getWriter(bytes).write(stream, sock);
				}
				catch (System.Exception e)
				{
					com.db4o.Msg.NULL.write(stream, sock);
				}
			}
			return true;
		}
	}
}
