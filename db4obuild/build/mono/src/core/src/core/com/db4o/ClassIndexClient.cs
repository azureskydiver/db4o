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
	/// <summary>client class index.</summary>
	/// <remarks>
	/// client class index. Largly intended to do nothing or
	/// redirect functionality to the server.
	/// </remarks>
	internal sealed class ClassIndexClient : com.db4o.ClassIndex
	{
		private readonly com.db4o.YapClass i_yapClass;

		internal ClassIndexClient(com.db4o.YapClass aYapClass)
		{
			i_yapClass = aYapClass;
		}

		internal override void add(int a_id)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal override long[] getInternalIDs(com.db4o.Transaction trans, int yapClassID
			)
		{
			com.db4o.YapClient stream = (com.db4o.YapClient)i_yapClass.getStream();
			stream.writeMsg(com.db4o.Msg.GET_INTERNAL_IDS.getWriterForInt(trans, yapClassID));
			com.db4o.YapWriter reader = stream.expectedByteResponse(com.db4o.Msg.ID_LIST);
			int size = reader.readInt();
			long[] ids = new long[size];
			for (int i = 0; i < size; i++)
			{
				ids[i] = reader.readInt();
			}
			return ids;
		}

		internal override void read(com.db4o.Transaction a_trans)
		{
		}

		internal override void setDirty(com.db4o.YapStream a_stream)
		{
		}

		internal override void setID(com.db4o.YapStream a_stream, int a_id)
		{
		}

		internal void write(com.db4o.YapStream a_stream)
		{
		}

		internal sealed override void writeOwnID(com.db4o.YapWriter a_writer)
		{
			a_writer.writeInt(0);
		}
	}
}
