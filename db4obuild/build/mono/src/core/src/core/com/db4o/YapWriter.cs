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
	/// <summary>public for .NET conversion reasons</summary>
	/// <exclude></exclude>
	public sealed class YapWriter : com.db4o.YapReader
	{
		private int i_address;

		private int _addressOffset;

		private int i_cascadeDelete;

		private com.db4o.Tree i_embedded;

		private int i_id;

		private int i_instantionDepth;

		private int i_length;

		internal com.db4o.Transaction i_trans;

		private int i_updateDepth = 1;

		internal YapWriter(com.db4o.Transaction a_trans, int a_initialBufferSize)
		{
			i_trans = a_trans;
			i_length = a_initialBufferSize;
			_buffer = new byte[i_length];
		}

		internal YapWriter(com.db4o.Transaction a_trans, int a_address, int a_initialBufferSize
			) : this(a_trans, a_initialBufferSize)
		{
			i_address = a_address;
		}

		internal YapWriter(com.db4o.YapWriter parent, com.db4o.YapWriter[] previousRead, 
			int previousCount)
		{
			previousRead[previousCount++] = this;
			int parentID = parent.readInt();
			i_length = parent.readInt();
			i_id = parent.readInt();
			previousRead[parentID].addEmbedded(this);
			i_address = parent.readInt();
			i_trans = parent.getTransaction();
			_buffer = new byte[i_length];
			j4o.lang.JavaSystem.arraycopy(parent._buffer, parent._offset, _buffer, 0, i_length
				);
			parent._offset += i_length;
			if (previousCount < previousRead.Length)
			{
				new com.db4o.YapWriter(parent, previousRead, previousCount);
			}
		}

		internal void addEmbedded(com.db4o.YapWriter a_bytes)
		{
			i_embedded = com.db4o.Tree.add(i_embedded, new com.db4o.TreeIntObject(a_bytes.getID
				(), a_bytes));
		}

		internal int appendTo(com.db4o.YapWriter a_bytes, int a_id)
		{
			a_id++;
			a_bytes.writeInt(i_length);
			a_bytes.writeInt(i_id);
			a_bytes.writeInt(i_address);
			a_bytes.append(_buffer);
			int[] newID = { a_id };
			int myID = a_id;
			forEachEmbedded(new _AnonymousInnerClass85(this, a_bytes, myID, newID));
			return newID[0];
		}

		private sealed class _AnonymousInnerClass85 : com.db4o.VisitorYapBytes
		{
			public _AnonymousInnerClass85(YapWriter _enclosing, com.db4o.YapWriter a_bytes, int
				 myID, int[] newID)
			{
				this._enclosing = _enclosing;
				this.a_bytes = a_bytes;
				this.myID = myID;
				this.newID = newID;
			}

			public void visit(com.db4o.YapWriter a_embedded)
			{
				a_bytes.writeInt(myID);
				newID[0] = a_embedded.appendTo(a_bytes, newID[0]);
			}

			private readonly YapWriter _enclosing;

			private readonly com.db4o.YapWriter a_bytes;

			private readonly int myID;

			private readonly int[] newID;
		}

		internal int cascadeDeletes()
		{
			return i_cascadeDelete;
		}

		internal void debugCheckBytes()
		{
		}

		internal int embeddedCount()
		{
			int[] count = { 0 };
			forEachEmbedded(new _AnonymousInnerClass110(this, count));
			return count[0];
		}

		private sealed class _AnonymousInnerClass110 : com.db4o.VisitorYapBytes
		{
			public _AnonymousInnerClass110(YapWriter _enclosing, int[] count)
			{
				this._enclosing = _enclosing;
				this.count = count;
			}

			public void visit(com.db4o.YapWriter a_bytes)
			{
				count[0] += 1 + a_bytes.embeddedCount();
			}

			private readonly YapWriter _enclosing;

			private readonly int[] count;
		}

		internal int embeddedLength()
		{
			int[] length = { 0 };
			forEachEmbedded(new _AnonymousInnerClass120(this, length));
			return length[0];
		}

		private sealed class _AnonymousInnerClass120 : com.db4o.VisitorYapBytes
		{
			public _AnonymousInnerClass120(YapWriter _enclosing, int[] length)
			{
				this._enclosing = _enclosing;
				this.length = length;
			}

			public void visit(com.db4o.YapWriter a_bytes)
			{
				length[0] += a_bytes.getLength() + a_bytes.embeddedLength();
			}

			private readonly YapWriter _enclosing;

			private readonly int[] length;
		}

		internal void forEachEmbedded(com.db4o.VisitorYapBytes a_visitor)
		{
			if (i_embedded != null)
			{
				i_embedded.traverse(new _AnonymousInnerClass130(this, a_visitor));
			}
		}

		private sealed class _AnonymousInnerClass130 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass130(YapWriter _enclosing, com.db4o.VisitorYapBytes a_visitor
				)
			{
				this._enclosing = _enclosing;
				this.a_visitor = a_visitor;
			}

			public void visit(object a_object)
			{
				a_visitor.visit((com.db4o.YapWriter)((com.db4o.TreeIntObject)a_object).i_object);
			}

			private readonly YapWriter _enclosing;

			private readonly com.db4o.VisitorYapBytes a_visitor;
		}

		internal int getAddress()
		{
			return i_address;
		}

		internal int addressOffset()
		{
			return _addressOffset;
		}

		internal int getID()
		{
			return i_id;
		}

		internal int getInstantiationDepth()
		{
			return i_instantionDepth;
		}

		internal override int getLength()
		{
			return i_length;
		}

		internal com.db4o.YapStream getStream()
		{
			return i_trans.i_stream;
		}

		internal com.db4o.Transaction getTransaction()
		{
			return i_trans;
		}

		internal int getUpdateDepth()
		{
			return i_updateDepth;
		}

		internal byte[] getWrittenBytes()
		{
			byte[] bytes = new byte[_offset];
			j4o.lang.JavaSystem.arraycopy(_buffer, 0, bytes, 0, _offset);
			return bytes;
		}

		internal void read()
		{
			i_trans.i_stream.readBytes(_buffer, i_address, _addressOffset, i_length);
		}

		internal bool read(com.db4o.YapSocket sock)
		{
			int offset = 0;
			int length = i_length;
			while (length > 0)
			{
				int read = sock.read(_buffer, offset, length);
				if (read < 0)
				{
					return false;
				}
				offset += read;
				length -= read;
			}
			return true;
		}

		internal com.db4o.YapWriter readEmbeddedObject()
		{
			int id = readInt();
			int length = readInt();
			com.db4o.Tree tio = com.db4o.TreeInt.find(i_embedded, id);
			if (tio != null)
			{
				return (com.db4o.YapWriter)((com.db4o.TreeIntObject)tio).i_object;
			}
			com.db4o.YapWriter bytes = i_trans.i_stream.readObjectWriterByAddress(i_trans, id
				, length);
			if (bytes != null)
			{
				bytes.setID(id);
			}
			return bytes;
		}

		internal com.db4o.YapWriter readYapBytes()
		{
			int length = readInt();
			if (length == 0)
			{
				return null;
			}
			com.db4o.YapWriter yb = new com.db4o.YapWriter(i_trans, length);
			j4o.lang.JavaSystem.arraycopy(_buffer, _offset, yb._buffer, 0, length);
			_offset += length;
			return yb;
		}

		internal void removeFirstBytes(int aLength)
		{
			i_length -= aLength;
			byte[] temp = new byte[i_length];
			j4o.lang.JavaSystem.arraycopy(_buffer, aLength, temp, 0, i_length);
			_buffer = temp;
			_offset -= aLength;
			if (_offset < 0)
			{
				_offset = 0;
			}
		}

		internal void address(int a_address)
		{
			i_address = a_address;
		}

		internal void setCascadeDeletes(int depth)
		{
			i_cascadeDelete = depth;
		}

		internal void setID(int a_id)
		{
			i_id = a_id;
		}

		internal void setInstantiationDepth(int a_depth)
		{
			i_instantionDepth = a_depth;
		}

		internal void setTransaction(com.db4o.Transaction aTrans)
		{
			i_trans = aTrans;
		}

		internal void setUpdateDepth(int a_depth)
		{
			i_updateDepth = a_depth;
		}

		internal void trim4(int a_offset, int a_length)
		{
			byte[] temp = new byte[a_length];
			j4o.lang.JavaSystem.arraycopy(_buffer, a_offset, temp, 0, a_length);
			_buffer = temp;
			i_length = a_length;
		}

		internal void useSlot(int a_adress)
		{
			i_address = a_adress;
			_offset = 0;
		}

		internal void useSlot(int a_adress, int a_length)
		{
			i_address = a_adress;
			_offset = 0;
			if (a_length > _buffer.Length)
			{
				_buffer = new byte[a_length];
			}
			i_length = a_length;
		}

		internal void useSlot(int a_id, int a_adress, int a_length)
		{
			i_id = a_id;
			useSlot(a_adress, a_length);
		}

		internal void write()
		{
			i_trans.i_file.writeBytes(this);
		}

		internal void writeEmbedded()
		{
			com.db4o.YapWriter finalThis = this;
			forEachEmbedded(new _AnonymousInnerClass290(this, finalThis));
			i_embedded = null;
		}

		private sealed class _AnonymousInnerClass290 : com.db4o.VisitorYapBytes
		{
			public _AnonymousInnerClass290(YapWriter _enclosing, com.db4o.YapWriter finalThis
				)
			{
				this._enclosing = _enclosing;
				this.finalThis = finalThis;
			}

			public void visit(com.db4o.YapWriter a_bytes)
			{
				a_bytes.writeEmbedded();
				this._enclosing.i_trans.i_stream.writeEmbedded(finalThis, a_bytes);
			}

			private readonly YapWriter _enclosing;

			private readonly com.db4o.YapWriter finalThis;
		}

		internal void writeEmbeddedNull()
		{
			writeInt(0);
			writeInt(0);
		}

		internal void writeEncrypt()
		{
			i_trans.i_stream.i_handlers.encrypt(this);
			i_trans.i_file.writeBytes(this);
			i_trans.i_stream.i_handlers.decrypt(this);
		}

		internal void writeQueryResult(com.db4o.QResult a_qr)
		{
			int size = a_qr.size();
			writeInt(size);
			_offset += (size - 1) * com.db4o.YapConst.YAPID_LENGTH;
			int dec = com.db4o.YapConst.YAPID_LENGTH * 2;
			for (int i = 0; i < size; i++)
			{
				writeInt(a_qr.nextInt());
				_offset -= dec;
			}
		}

		internal void writeShortString(string a_string)
		{
			i_trans.i_stream.i_handlers.i_stringHandler.writeShort(a_string, this);
		}

		public void moveForward(int length)
		{
			_addressOffset += length;
		}

		public void writeForward()
		{
			write();
			_addressOffset += i_length;
			_offset = 0;
		}
	}
}
