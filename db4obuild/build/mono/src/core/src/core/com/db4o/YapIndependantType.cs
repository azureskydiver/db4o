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
	/// <summary>
	/// Common base class for YapString and YapArray:
	/// There is one indirection in the database file to this.
	/// </summary>
	/// <remarks>
	/// Common base class for YapString and YapArray:
	/// There is one indirection in the database file to this.
	/// </remarks>
	internal abstract class YapIndependantType : com.db4o.YapDataType
	{
		internal readonly com.db4o.YapStream _stream;

		public YapIndependantType(com.db4o.YapStream stream)
		{
			_stream = stream;
		}

		internal com.db4o.YapWriter i_lastIo;

		public void copyValue(object a_from, object a_to)
		{
		}

		/// <summary>overriden in YapArray</summary>
		public virtual void deleteEmbedded(com.db4o.YapWriter a_bytes)
		{
			int address = a_bytes.readInt();
			int length = a_bytes.readInt();
			if (address > 0)
			{
				a_bytes.getTransaction().freeOnCommit(address, address, length);
			}
		}

		public com.db4o.reflect.ReflectClass primitiveClassReflector()
		{
			return null;
		}

		public virtual object readIndexObject(com.db4o.YapWriter a_writer)
		{
			return read(a_writer);
		}

		public virtual object indexEntry(object a_object)
		{
			if (a_object == null)
			{
				return null;
			}
			return new int[] { i_lastIo.getAddress(), i_lastIo.getLength() };
		}

		public int linkLength()
		{
			return com.db4o.YapConst.YAPINT_LENGTH + com.db4o.YapConst.YAPID_LENGTH;
		}

		public abstract com.db4o.YapComparable prepareComparison(object arg1);

		public abstract int compareTo(object arg1);

		public abstract bool isEqual(object arg1);

		public abstract bool isGreater(object arg1);

		public abstract bool isSmaller(object arg1);

		public abstract void appendEmbedded3(com.db4o.YapWriter arg1);

		public abstract bool canHold(com.db4o.reflect.ReflectClass arg1);

		public abstract void cascadeActivation(com.db4o.Transaction arg1, object arg2, int
			 arg3, bool arg4);

		public abstract com.db4o.reflect.ReflectClass classReflector();

		public abstract int getID();

		public abstract bool equals(com.db4o.YapDataType arg1);

		public abstract object indexObject(com.db4o.Transaction arg1, object arg2);

		public abstract void prepareLastIoComparison(com.db4o.Transaction arg1, object arg2
			);

		public abstract object read(com.db4o.YapWriter arg1);

		public abstract object readQuery(com.db4o.Transaction arg1, com.db4o.YapReader arg2
			, bool arg3);

		public abstract bool supportsIndex();

		public abstract void writeNew(object arg1, com.db4o.YapWriter arg2);

		public abstract int getType();

		public abstract com.db4o.YapClass getYapClass(com.db4o.YapStream arg1);

		public abstract void readCandidates(com.db4o.YapReader arg1, com.db4o.QCandidates
			 arg2);

		public abstract object readIndexEntry(com.db4o.YapReader arg1);

		public abstract com.db4o.YapDataType readArrayWrapper(com.db4o.Transaction arg1, 
			com.db4o.YapReader[] arg2);

		public abstract void writeIndexEntry(com.db4o.YapWriter arg1, object arg2);
	}
}
