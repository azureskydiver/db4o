namespace com.db4o
{
	internal class YapArray : com.db4o.YapIndependantType
	{
		internal readonly com.db4o.YapStream _stream;

		internal readonly com.db4o.TypeHandler4 i_handler;

		internal readonly bool i_isPrimitive;

		internal readonly com.db4o.reflect.ReflectArray _reflectArray;

		internal YapArray(com.db4o.YapStream stream, com.db4o.TypeHandler4 a_handler, bool
			 a_isPrimitive) : base(stream)
		{
			_stream = stream;
			i_handler = a_handler;
			i_isPrimitive = a_isPrimitive;
			_reflectArray = stream.reflector().array();
		}

		internal virtual object[] allElements(object a_object)
		{
			object[] all = new object[_reflectArray.getLength(a_object)];
			for (int i = all.Length - 1; i >= 0; i--)
			{
				all[i] = _reflectArray.get(a_object, i);
			}
			return all;
		}

		public override void appendEmbedded3(com.db4o.YapWriter a_bytes)
		{
			a_bytes.incrementOffset(linkLength());
		}

		public override bool canHold(com.db4o.reflect.ReflectClass claxx)
		{
			return i_handler.canHold(claxx);
		}

		public sealed override void cascadeActivation(com.db4o.Transaction a_trans, object
			 a_object, int a_depth, bool a_activate)
		{
			if (i_handler is com.db4o.YapClass)
			{
				a_depth--;
				object[] all = allElements(a_object);
				if (a_activate)
				{
					for (int i = all.Length - 1; i >= 0; i--)
					{
						_stream.stillToActivate(all[i], a_depth);
					}
				}
				else
				{
					for (int i = all.Length - 1; i >= 0; i--)
					{
						_stream.stillToDeactivate(all[i], a_depth, false);
					}
				}
			}
		}

		public override com.db4o.reflect.ReflectClass classReflector()
		{
			return i_handler.classReflector();
		}

		internal virtual com.db4o.TreeInt collectIDs(com.db4o.TreeInt tree, com.db4o.YapWriter
			 a_bytes)
		{
			com.db4o.Transaction trans = a_bytes.getTransaction();
			com.db4o.YapReader bytes = a_bytes.readEmbeddedObject(trans);
			if (bytes != null)
			{
				int count = elementCount(trans, bytes);
				for (int i = 0; i < count; i++)
				{
					tree = (com.db4o.TreeInt)com.db4o.Tree.add(tree, new com.db4o.TreeInt(bytes.readInt
						()));
				}
			}
			return tree;
		}

		public sealed override void deleteEmbedded(com.db4o.YapWriter a_bytes)
		{
			int address = a_bytes.readInt();
			int length = a_bytes.readInt();
			if (address > 0)
			{
				com.db4o.Transaction trans = a_bytes.getTransaction();
				if (a_bytes.cascadeDeletes() > 0 && i_handler is com.db4o.YapClass)
				{
					com.db4o.YapWriter bytes = a_bytes.getStream().readObjectWriterByAddress(trans, address
						, length);
					if (bytes != null)
					{
						bytes.setCascadeDeletes(a_bytes.cascadeDeletes());
						for (int i = elementCount(trans, bytes); i > 0; i--)
						{
							i_handler.deleteEmbedded(bytes);
						}
					}
				}
				trans.freeOnCommit(address, address, length);
			}
		}

		public void deletePrimitiveEmbedded(com.db4o.YapWriter a_bytes, com.db4o.YapClassPrimitive
			 a_classPrimitive)
		{
			int address = a_bytes.readInt();
			int length = a_bytes.readInt();
			if (address > 0)
			{
				com.db4o.Transaction trans = a_bytes.getTransaction();
				com.db4o.YapWriter bytes = a_bytes.getStream().readObjectWriterByAddress(trans, address
					, length);
				if (bytes != null)
				{
					for (int i = elementCount(trans, bytes); i > 0; i--)
					{
						int id = bytes.readInt();
						int[] addressLength = new int[2];
						trans.getSlotInformation(id, addressLength);
						a_classPrimitive.free(trans, id, addressLength[0], addressLength[1]);
					}
				}
				trans.freeOnCommit(address, address, length);
			}
		}

		internal virtual int elementCount(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_bytes)
		{
			int typeOrLength = a_bytes.readInt();
			if (typeOrLength >= 0)
			{
				return typeOrLength;
			}
			return a_bytes.readInt();
		}

		public sealed override bool equals(com.db4o.TypeHandler4 a_dataType)
		{
			if (a_dataType is com.db4o.YapArray)
			{
				if (((com.db4o.YapArray)a_dataType).identifier() == identifier())
				{
					return (i_handler.equals(((com.db4o.YapArray)a_dataType).i_handler));
				}
			}
			return false;
		}

		public sealed override int getID()
		{
			return i_handler.getID();
		}

		public override int getType()
		{
			return i_handler.getType();
		}

		public override com.db4o.YapClass getYapClass(com.db4o.YapStream a_stream)
		{
			return i_handler.getYapClass(a_stream);
		}

		internal virtual byte identifier()
		{
			return com.db4o.YapConst.YAPARRAY;
		}

		public override object comparableObject(com.db4o.Transaction a_trans, object a_object
			)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal virtual int objectLength(object a_object)
		{
			return com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst.YAPINT_LENGTH * (com.db4o.Debug
				.arrayTypes ? 2 : 1) + (_reflectArray.getLength(a_object) * i_handler.linkLength
				());
		}

		public override void prepareLastIoComparison(com.db4o.Transaction a_trans, object
			 obj)
		{
			prepareComparison(obj);
		}

		public override object read(com.db4o.YapWriter a_bytes)
		{
			com.db4o.YapWriter bytes = a_bytes.readEmbeddedObject();
			i_lastIo = bytes;
			if (bytes == null)
			{
				return null;
			}
			bytes.setUpdateDepth(a_bytes.getUpdateDepth());
			bytes.setInstantiationDepth(a_bytes.getInstantiationDepth());
			object array = read1(bytes);
			return array;
		}

		public override object readIndexEntry(com.db4o.YapReader a_reader)
		{
			throw com.db4o.YapConst.virtualException();
		}

		public override object readQuery(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader, bool a_toArray)
		{
			com.db4o.YapReader bytes = a_reader.readEmbeddedObject(a_trans);
			if (bytes == null)
			{
				return null;
			}
			object array = read1Query(a_trans, bytes);
			return array;
		}

		internal virtual object read1Query(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader)
		{
			int[] elements = new int[1];
			object ret = readCreate(a_trans, a_reader, elements);
			if (ret != null)
			{
				for (int i = 0; i < elements[0]; i++)
				{
					_reflectArray.set(ret, i, i_handler.readQuery(a_trans, a_reader, true));
				}
			}
			return ret;
		}

		internal virtual object read1(com.db4o.YapWriter a_bytes)
		{
			int[] elements = new int[1];
			object ret = readCreate(a_bytes.getTransaction(), a_bytes, elements);
			if (ret != null)
			{
				if (i_handler.readArray(ret, a_bytes))
				{
					return ret;
				}
				for (int i = 0; i < elements[0]; i++)
				{
					_reflectArray.set(ret, i, i_handler.read(a_bytes));
				}
			}
			return ret;
		}

		private object readCreate(com.db4o.Transaction a_trans, com.db4o.YapReader a_reader
			, int[] a_elements)
		{
			com.db4o.reflect.ReflectClass[] clazz = new com.db4o.reflect.ReflectClass[1];
			a_elements[0] = readElementsAndClass(a_trans, a_reader, clazz);
			if (i_isPrimitive)
			{
				return _reflectArray.newInstance(i_handler.primitiveClassReflector(), a_elements[
					0]);
			}
			else
			{
				if (clazz[0] != null)
				{
					return _reflectArray.newInstance(clazz[0], a_elements[0]);
				}
			}
			return null;
		}

		public override com.db4o.TypeHandler4 readArrayWrapper(com.db4o.Transaction a_trans
			, com.db4o.YapReader[] a_bytes)
		{
			return this;
		}

		public override void readCandidates(com.db4o.YapReader a_bytes, com.db4o.QCandidates
			 a_candidates)
		{
			com.db4o.YapReader bytes = a_bytes.readEmbeddedObject(a_candidates.i_trans);
			if (bytes != null)
			{
				int count = elementCount(a_candidates.i_trans, bytes);
				for (int i = 0; i < count; i++)
				{
					a_candidates.addByIdentity(new com.db4o.QCandidate(a_candidates, null, bytes.readInt
						(), true));
				}
			}
		}

		internal virtual int readElementsAndClass(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_bytes, com.db4o.reflect.ReflectClass[] clazz)
		{
			int elements = a_bytes.readInt();
			clazz[0] = i_handler.classReflector();
			if (com.db4o.Debug.arrayTypes && elements < 0)
			{
				if (elements != com.db4o.YapConst.IGNORE_ID)
				{
					bool primitive = false;
					com.db4o.YapClass yc = a_trans.i_stream.getYapClass(-elements);
					if (yc != null)
					{
						if (primitive)
						{
							clazz[0] = yc.primitiveClassReflector();
						}
						else
						{
							clazz[0] = yc.classReflector();
						}
					}
				}
				elements = a_bytes.readInt();
			}
			if (com.db4o.Debug.exceedsMaximumArrayEntries(elements, i_isPrimitive))
			{
				return 0;
			}
			return elements;
		}

		internal static object[] toArray(com.db4o.YapStream a_stream, object a_object)
		{
			if (a_object != null)
			{
				com.db4o.reflect.ReflectClass claxx = a_stream.reflector().forObject(a_object);
				if (claxx.isArray())
				{
					com.db4o.YapArray ya;
					if (a_stream.reflector().array().isNDimensional(claxx))
					{
						ya = new com.db4o.YapArrayN(a_stream, null, false);
					}
					else
					{
						ya = new com.db4o.YapArray(a_stream, null, false);
					}
					return ya.allElements(a_object);
				}
			}
			return new object[0];
		}

		internal virtual void writeClass(object a_object, com.db4o.YapWriter a_bytes)
		{
			int yapClassID = 0;
			com.db4o.reflect.Reflector reflector = a_bytes.i_trans.reflector();
			com.db4o.reflect.ReflectClass claxx = _reflectArray.getComponentType(reflector.forObject
				(a_object));
			bool primitive = false;
			com.db4o.YapStream stream = a_bytes.getStream();
			if (primitive)
			{
				claxx = stream.i_handlers.handlerForClass(stream, claxx).classReflector();
			}
			com.db4o.YapClass yc = stream.getYapClass(claxx, true);
			if (yc != null)
			{
				yapClassID = yc.getID();
			}
			if (yapClassID == 0)
			{
				yapClassID = -com.db4o.YapConst.IGNORE_ID;
			}
			else
			{
				if (primitive)
				{
					yapClassID -= com.db4o.YapConst.PRIMITIVE;
				}
			}
			a_bytes.writeInt(-yapClassID);
		}

		public override void writeIndexEntry(com.db4o.YapWriter a_writer, object a_object
			)
		{
			throw com.db4o.YapConst.virtualException();
		}

		public override int writeNew(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (a_object == null)
			{
				a_bytes.writeEmbeddedNull();
			}
			else
			{
				int length = objectLength(a_object);
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(a_bytes.getTransaction(), length
					);
				bytes.setUpdateDepth(a_bytes.getUpdateDepth());
				writeNew1(a_object, bytes);
				bytes.setID(a_bytes._offset);
				i_lastIo = bytes;
				a_bytes.getStream().writeEmbedded(a_bytes, bytes);
				a_bytes.incrementOffset(com.db4o.YapConst.YAPID_LENGTH);
				a_bytes.writeInt(length);
			}
			return -1;
		}

		internal virtual void writeNew1(object a_object, com.db4o.YapWriter a_bytes)
		{
			writeClass(a_object, a_bytes);
			int elements = _reflectArray.getLength(a_object);
			a_bytes.writeInt(elements);
			if (i_handler.writeArray(a_object, a_bytes))
			{
				return;
			}
			for (int i = 0; i < elements; i++)
			{
				i_handler.writeNew(_reflectArray.get(a_object, i), a_bytes);
			}
		}

		public override com.db4o.YapComparable prepareComparison(object obj)
		{
			i_handler.prepareComparison(obj);
			return this;
		}

		public override int compareTo(object a_obj)
		{
			return -1;
		}

		public override bool isEqual(object obj)
		{
			object[] compareWith = allElements(obj);
			for (int j = 0; j < compareWith.Length; j++)
			{
				if (i_handler.isEqual(compareWith[j]))
				{
					return true;
				}
			}
			return false;
		}

		public override bool isGreater(object obj)
		{
			object[] compareWith = allElements(obj);
			for (int j = 0; j < compareWith.Length; j++)
			{
				if (i_handler.isGreater(compareWith[j]))
				{
					return true;
				}
			}
			return false;
		}

		public override bool isSmaller(object obj)
		{
			object[] compareWith = allElements(obj);
			for (int j = 0; j < compareWith.Length; j++)
			{
				if (i_handler.isSmaller(compareWith[j]))
				{
					return true;
				}
			}
			return false;
		}

		public override bool supportsIndex()
		{
			return false;
		}
	}
}
