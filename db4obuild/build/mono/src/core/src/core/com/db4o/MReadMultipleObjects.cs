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
	internal sealed class MReadMultipleObjects : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.YapSocket sock)
		{
			int size = readInt();
			com.db4o.MsgD[] ret = new com.db4o.MsgD[size];
			int length = (1 + size) * com.db4o.YapConst.YAPINT_LENGTH;
			com.db4o.YapStream stream = getStream();
			com.db4o.YapWriter bytes = null;
			lock (stream.i_lock)
			{
				for (int i = 0; i < size; i++)
				{
					int id = this.payLoad.readInt();
					try
					{
						bytes = stream.readWriterByID(getTransaction(), id);
					}
					catch (System.Exception e)
					{
						bytes = null;
					}
					if (bytes != null)
					{
						try
						{
							com.db4o.YapClassAny.appendEmbedded(bytes);
						}
						catch (System.Exception e)
						{
						}
						ret[i] = com.db4o.Msg.OBJECT_TO_CLIENT.getWriter(bytes);
						length += ret[i].payLoad.getLength();
					}
				}
			}
			com.db4o.MsgD multibytes = com.db4o.Msg.READ_MULTIPLE_OBJECTS.getWriterForLength(
				getTransaction(), length);
			multibytes.writeInt(size);
			for (int i = 0; i < size; i++)
			{
				if (ret[i] == null)
				{
					multibytes.writeInt(0);
				}
				else
				{
					multibytes.writeInt(ret[i].payLoad.getLength());
					multibytes.payLoad.append(ret[i].payLoad._buffer);
				}
			}
			multibytes.write(stream, sock);
			return true;
		}
	}
}
