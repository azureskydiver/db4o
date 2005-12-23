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
	/// <exclude></exclude>
	public abstract class YapIndependantType : com.db4o.TypeHandler4
	{
		internal readonly com.db4o.YapStream _stream;

		public YapIndependantType(com.db4o.YapStream stream)
		{
			_stream = stream;
		}

		internal com.db4o.YapWriter i_lastIo;

		public virtual object coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return canHold(claxx) ? obj : com.db4o.foundation.No4.INSTANCE;
		}

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

		public com.db4o.reflect.ReflectClass primitiveClassReflector()
		{
			return null;
		}

		public virtual bool readArray(object array, com.db4o.YapWriter reader)
		{
			return false;
		}

		public virtual object readIndexValueOrID(com.db4o.YapWriter a_writer)
		{
			return read(a_writer);
		}

		public virtual bool writeArray(object array, com.db4o.YapWriter reader)
		{
			return false;
		}

		public abstract bool isGreater(object obj);

		public abstract com.db4o.YapComparable prepareComparison(object obj);

		public abstract int compareTo(object obj);

		public abstract bool isEqual(object obj);

		public abstract bool isSmaller(object obj);

		public abstract object comparableObject(com.db4o.Transaction trans, object indexEntry
			);

		public abstract object readIndexEntry(com.db4o.YapReader a_reader);

		public abstract void writeIndexEntry(com.db4o.YapWriter a_writer, object a_object
			);

		public abstract void appendEmbedded3(com.db4o.YapWriter arg1);

		public abstract bool canHold(com.db4o.reflect.ReflectClass arg1);

		public abstract void cascadeActivation(com.db4o.Transaction arg1, object arg2, int
			 arg3, bool arg4);

		public abstract com.db4o.reflect.ReflectClass classReflector();

		public abstract int getID();

		public abstract bool equals(com.db4o.TypeHandler4 arg1);

		public abstract bool indexNullHandling();

		public abstract void prepareLastIoComparison(com.db4o.Transaction arg1, object arg2
			);

		public abstract object read(com.db4o.YapWriter arg1);

		public abstract object readQuery(com.db4o.Transaction arg1, com.db4o.YapReader arg2
			, bool arg3);

		public abstract bool supportsIndex();

		public abstract int writeNew(object arg1, com.db4o.YapWriter arg2);

		public abstract int getType();

		public abstract com.db4o.YapClass getYapClass(com.db4o.YapStream arg1);

		public abstract void readCandidates(com.db4o.YapReader arg1, com.db4o.QCandidates
			 arg2);

		public abstract com.db4o.TypeHandler4 readArrayWrapper(com.db4o.Transaction arg1, 
			com.db4o.YapReader[] arg2);
	}
}
