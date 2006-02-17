namespace com.db4o
{
	internal class YapClassPrimitive : com.db4o.YapClass
	{
		internal readonly com.db4o.TypeHandler4 i_handler;

		internal YapClassPrimitive(com.db4o.YapStream a_stream, com.db4o.TypeHandler4 a_handler
			) : base(a_stream, a_handler.classReflector())
		{
			i_fields = com.db4o.YapField.EMPTY_ARRAY;
			i_handler = a_handler;
			i_objectLength = memberLength();
		}

		internal override void activateFields(com.db4o.Transaction a_trans, object a_object
			, int a_depth)
		{
		}

		internal sealed override void addToIndex(com.db4o.YapFile a_stream, com.db4o.Transaction
			 a_trans, int a_id)
		{
		}

		internal override bool allowsQueries()
		{
			return false;
		}

		public override void appendEmbedded1(com.db4o.YapWriter a_bytes)
		{
		}

		internal override void cacheDirty(com.db4o.foundation.Collection4 col)
		{
		}

		public override bool canHold(com.db4o.reflect.ReflectClass claxx)
		{
			return i_handler.canHold(claxx);
		}

		public override com.db4o.reflect.ReflectClass classReflector()
		{
			return i_handler.classReflector();
		}

		internal override void deleteEmbedded1(com.db4o.YapWriter a_bytes, int a_id)
		{
			if (i_handler is com.db4o.YapArray)
			{
				com.db4o.YapArray ya = (com.db4o.YapArray)i_handler;
				if (ya.i_isPrimitive)
				{
					ya.deletePrimitiveEmbedded(a_bytes, this);
					a_bytes.slotDelete();
					return;
				}
			}
			if (i_handler is com.db4o.YapClassAny)
			{
				a_bytes.incrementOffset(i_handler.linkLength());
			}
			else
			{
				i_handler.deleteEmbedded(a_bytes);
			}
			free(a_bytes, a_id);
		}

		internal override void deleteMembers(com.db4o.YapWriter a_bytes, int a_type)
		{
			if (a_type == com.db4o.YapConst.TYPE_ARRAY)
			{
				new com.db4o.YapArray(a_bytes.getStream(), this, true).deletePrimitiveEmbedded(a_bytes
					, this);
			}
			else
			{
				if (a_type == com.db4o.YapConst.TYPE_NARRAY)
				{
					new com.db4o.YapArrayN(a_bytes.getStream(), this, true).deletePrimitiveEmbedded(a_bytes
						, this);
				}
			}
		}

		internal void free(com.db4o.Transaction a_trans, int a_id, int a_address, int a_length
			)
		{
			a_trans.slotFreePointerOnCommit(a_id, a_address, a_length);
		}

		internal void free(com.db4o.YapWriter a_bytes, int a_id)
		{
			a_bytes.getTransaction().slotFreePointerOnCommit(a_id, a_bytes.getAddress(), a_bytes
				.getLength());
		}

		internal sealed override com.db4o.ClassIndex getIndex()
		{
			return null;
		}

		internal override bool hasIndex()
		{
			return false;
		}

		internal override object instantiate(com.db4o.YapObject a_yapObject, object a_object
			, com.db4o.YapWriter a_bytes, bool a_addToIDTree)
		{
			if (a_object == null)
			{
				try
				{
					a_object = i_handler.read(a_bytes);
				}
				catch (com.db4o.CorruptionException ce)
				{
					return null;
				}
				a_yapObject.setObjectWeak(a_bytes.getStream(), a_object);
			}
			a_yapObject.setStateClean();
			return a_object;
		}

		internal override object instantiateTransient(com.db4o.YapObject a_yapObject, object
			 a_object, com.db4o.YapWriter a_bytes)
		{
			try
			{
				return i_handler.read(a_bytes);
			}
			catch (com.db4o.CorruptionException ce)
			{
				return null;
			}
		}

		internal override void instantiateFields(com.db4o.YapObject a_yapObject, object a_onObject
			, com.db4o.YapWriter a_bytes)
		{
			object obj = null;
			try
			{
				obj = i_handler.read(a_bytes);
			}
			catch (com.db4o.CorruptionException ce)
			{
				obj = null;
			}
			if (obj != null)
			{
				i_handler.copyValue(obj, a_onObject);
			}
		}

		public override bool isArray()
		{
			return i_id == com.db4o.YapHandlers.ANY_ARRAY_ID || i_id == com.db4o.YapHandlers.
				ANY_ARRAY_N_ID;
		}

		internal override bool isPrimitive()
		{
			return true;
		}

		internal override bool isStrongTyped()
		{
			return false;
		}

		internal override void marshall(com.db4o.YapObject a_yapObject, object a_object, 
			com.db4o.YapWriter a_bytes, bool a_new)
		{
			i_handler.writeNew(a_object, a_bytes);
		}

		internal override void marshallNew(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, object a_object)
		{
			i_handler.writeNew(a_object, a_bytes);
		}

		internal override int memberLength()
		{
			return i_handler.linkLength() + com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst
				.YAPID_LENGTH;
		}

		public override com.db4o.YapComparable prepareComparison(object a_constraint)
		{
			i_handler.prepareComparison(a_constraint);
			return i_handler;
		}

		public sealed override com.db4o.reflect.ReflectClass primitiveClassReflector()
		{
			return i_handler.primitiveClassReflector();
		}

		public override com.db4o.TypeHandler4 readArrayWrapper(com.db4o.Transaction a_trans
			, com.db4o.YapReader[] a_bytes)
		{
			if (isArray())
			{
				return i_handler;
			}
			return null;
		}

		internal override void removeFromIndex(com.db4o.Transaction ta, int id)
		{
		}

		public override bool supportsIndex()
		{
			return true;
		}

		internal sealed override bool writeObjectBegin()
		{
			return false;
		}
	}
}
