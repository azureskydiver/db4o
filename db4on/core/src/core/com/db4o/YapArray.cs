namespace com.db4o
{
	/// <exclude></exclude>
	public class YapArray : com.db4o.YapIndependantType
	{
		public readonly com.db4o.TypeHandler4 i_handler;

		public readonly bool i_isPrimitive;

		public readonly com.db4o.reflect.ReflectArray _reflectArray;

		public YapArray(com.db4o.YapStream stream, com.db4o.TypeHandler4 a_handler, bool 
			a_isPrimitive) : base(stream)
		{
			i_handler = a_handler;
			i_isPrimitive = a_isPrimitive;
			_reflectArray = stream.Reflector().Array();
		}

		public virtual object[] AllElements(object a_object)
		{
			object[] all = new object[_reflectArray.GetLength(a_object)];
			for (int i = all.Length - 1; i >= 0; i--)
			{
				all[i] = _reflectArray.Get(a_object, i);
			}
			return all;
		}

		public override bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			return i_handler.CanHold(claxx);
		}

		public sealed override void CascadeActivation(com.db4o.Transaction a_trans, object
			 a_object, int a_depth, bool a_activate)
		{
			if (i_handler is com.db4o.YapClass)
			{
				a_depth--;
				object[] all = AllElements(a_object);
				if (a_activate)
				{
					for (int i = all.Length - 1; i >= 0; i--)
					{
						_stream.StillToActivate(all[i], a_depth);
					}
				}
				else
				{
					for (int i = all.Length - 1; i >= 0; i--)
					{
						_stream.StillToDeactivate(all[i], a_depth, false);
					}
				}
			}
		}

		public override com.db4o.reflect.ReflectClass ClassReflector()
		{
			return i_handler.ClassReflector();
		}

		internal com.db4o.TreeInt CollectIDs(com.db4o.inside.marshall.MarshallerFamily mf
			, com.db4o.TreeInt tree, com.db4o.YapWriter reader)
		{
			return mf._array.CollectIDs(this, tree, reader);
		}

		public com.db4o.TreeInt CollectIDs1(com.db4o.Transaction trans, com.db4o.TreeInt 
			tree, com.db4o.YapReader reader)
		{
			if (reader != null)
			{
				int count = ElementCount(trans, reader);
				for (int i = 0; i < count; i++)
				{
					tree = (com.db4o.TreeInt)com.db4o.Tree.Add(tree, new com.db4o.TreeInt(reader.ReadInt
						()));
				}
			}
			return tree;
		}

		public override object ComparableObject(com.db4o.Transaction a_trans, object a_object
			)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public sealed override void DeleteEmbedded(com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapWriter a_bytes)
		{
			mf._array.DeleteEmbedded(this, a_bytes);
		}

		public void DeletePrimitiveEmbedded(com.db4o.YapWriter a_bytes, com.db4o.YapClassPrimitive
			 a_classPrimitive)
		{
			int address = a_bytes.ReadInt();
			int length = a_bytes.ReadInt();
			if (true)
			{
				return;
			}
			if (address > 0)
			{
				com.db4o.Transaction trans = a_bytes.GetTransaction();
				com.db4o.YapWriter bytes = a_bytes.GetStream().ReadObjectWriterByAddress(trans, address
					, length);
				if (bytes != null)
				{
					for (int i = ElementCount(trans, bytes); i > 0; i--)
					{
						int id = bytes.ReadInt();
						com.db4o.inside.slots.Slot slot = trans.GetSlotInformation(id);
						a_classPrimitive.Free(trans, id, slot._address, slot._length);
					}
				}
				trans.SlotFreeOnCommit(address, address, length);
			}
		}

		public virtual int ElementCount(com.db4o.Transaction a_trans, com.db4o.YapReader 
			a_bytes)
		{
			int typeOrLength = a_bytes.ReadInt();
			if (typeOrLength >= 0)
			{
				return typeOrLength;
			}
			return a_bytes.ReadInt();
		}

		public sealed override bool Equals(com.db4o.TypeHandler4 a_dataType)
		{
			if (a_dataType is com.db4o.YapArray)
			{
				if (((com.db4o.YapArray)a_dataType).Identifier() == Identifier())
				{
					return (i_handler.Equals(((com.db4o.YapArray)a_dataType).i_handler));
				}
			}
			return false;
		}

		public sealed override int GetID()
		{
			return i_handler.GetID();
		}

		public override int GetTypeID()
		{
			return i_handler.GetTypeID();
		}

		public override com.db4o.YapClass GetYapClass(com.db4o.YapStream a_stream)
		{
			return i_handler.GetYapClass(a_stream);
		}

		public virtual byte Identifier()
		{
			return com.db4o.YapConst.YAPARRAY;
		}

		public override object IndexEntryToObject(com.db4o.Transaction trans, object indexEntry
			)
		{
			return null;
		}

		public override bool IndexNullHandling()
		{
			return i_handler.IndexNullHandling();
		}

		public override int IsSecondClass()
		{
			return i_handler.IsSecondClass();
		}

		public override void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
			com.db4o.inside.marshall.MarshallerFamily.Current()._array.CalculateLengths(trans
				, header, this, obj, topLevel);
		}

		public virtual int ObjectLength(object obj)
		{
			return OwnLength(obj) + (_reflectArray.GetLength(obj) * i_handler.LinkLength());
		}

		public virtual int OwnLength(object obj)
		{
			return com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst.INT_LENGTH * 2;
		}

		public override void PrepareComparison(com.db4o.Transaction a_trans, object obj)
		{
			PrepareComparison(obj);
		}

		public sealed override object Read(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapWriter a_bytes, bool redirect)
		{
			return mf._array.Read(this, a_bytes);
		}

		public override object ReadIndexEntry(com.db4o.YapReader a_reader)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public sealed override object ReadQuery(com.db4o.Transaction a_trans, com.db4o.inside.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.YapReader a_reader, bool a_toArray)
		{
			return mf._array.ReadQuery(this, a_trans, a_reader);
		}

		public virtual object Read1Query(com.db4o.Transaction a_trans, com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapReader a_reader)
		{
			int[] elements = new int[1];
			object ret = ReadCreate(a_trans, a_reader, elements);
			if (ret != null)
			{
				for (int i = 0; i < elements[0]; i++)
				{
					_reflectArray.Set(ret, i, i_handler.ReadQuery(a_trans, mf, true, a_reader, true));
				}
			}
			return ret;
		}

		public virtual object Read1(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter
			 reader)
		{
			int[] elements = new int[1];
			object ret = ReadCreate(reader.GetTransaction(), reader, elements);
			if (ret != null)
			{
				if (i_handler.ReadArray(ret, reader))
				{
					return ret;
				}
				for (int i = 0; i < elements[0]; i++)
				{
					_reflectArray.Set(ret, i, i_handler.Read(mf, reader, true));
				}
			}
			return ret;
		}

		private object ReadCreate(com.db4o.Transaction a_trans, com.db4o.YapReader a_reader
			, int[] a_elements)
		{
			com.db4o.reflect.ReflectClass[] clazz = new com.db4o.reflect.ReflectClass[1];
			a_elements[0] = ReadElementsAndClass(a_trans, a_reader, clazz);
			if (i_isPrimitive)
			{
				return _reflectArray.NewInstance(i_handler.PrimitiveClassReflector(), a_elements[
					0]);
			}
			if (clazz[0] != null)
			{
				return _reflectArray.NewInstance(clazz[0], a_elements[0]);
			}
			return null;
		}

		public override com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction a_trans
			, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapReader[] a_bytes)
		{
			return this;
		}

		public override void ReadCandidates(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapReader reader, com.db4o.QCandidates candidates)
		{
			mf._array.ReadCandidates(this, reader, candidates);
		}

		public virtual void Read1Candidates(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapReader reader, com.db4o.QCandidates candidates)
		{
			int[] elements = new int[1];
			object ret = ReadCreate(candidates.i_trans, reader, elements);
			if (ret != null)
			{
				for (int i = 0; i < elements[0]; i++)
				{
					com.db4o.QCandidate qc = i_handler.ReadSubCandidate(mf, reader, candidates, true);
					if (qc != null)
					{
						candidates.AddByIdentity(qc);
					}
				}
			}
		}

		public override com.db4o.QCandidate ReadSubCandidate(com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapReader reader, com.db4o.QCandidates candidates, bool withIndirection
			)
		{
			reader.IncrementOffset(LinkLength());
			return null;
		}

		internal int ReadElementsAndClass(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_bytes, com.db4o.reflect.ReflectClass[] clazz)
		{
			int elements = a_bytes.ReadInt();
			clazz[0] = i_handler.ClassReflector();
			if (elements < 0)
			{
				if (elements != com.db4o.YapConst.IGNORE_ID)
				{
					bool primitive = false;
					com.db4o.YapClass yc = a_trans.Stream().GetYapClass(-elements);
					if (yc != null)
					{
						if (primitive)
						{
							clazz[0] = yc.PrimitiveClassReflector();
						}
						else
						{
							clazz[0] = yc.ClassReflector();
						}
					}
				}
				elements = a_bytes.ReadInt();
			}
			if (com.db4o.Debug.ExceedsMaximumArrayEntries(elements, i_isPrimitive))
			{
				return 0;
			}
			return elements;
		}

		internal static object[] ToArray(com.db4o.YapStream a_stream, object a_object)
		{
			if (a_object != null)
			{
				com.db4o.reflect.ReflectClass claxx = a_stream.Reflector().ForObject(a_object);
				if (claxx.IsArray())
				{
					com.db4o.YapArray ya;
					if (a_stream.Reflector().Array().IsNDimensional(claxx))
					{
						ya = new com.db4o.YapArrayN(a_stream, null, false);
					}
					else
					{
						ya = new com.db4o.YapArray(a_stream, null, false);
					}
					return ya.AllElements(a_object);
				}
			}
			return new object[0];
		}

		internal virtual void WriteClass(object a_object, com.db4o.YapWriter a_bytes)
		{
			int yapClassID = 0;
			com.db4o.reflect.Reflector reflector = a_bytes.i_trans.Reflector();
			com.db4o.reflect.ReflectClass claxx = _reflectArray.GetComponentType(reflector.ForObject
				(a_object));
			bool primitive = false;
			com.db4o.YapStream stream = a_bytes.GetStream();
			if (primitive)
			{
				claxx = stream.i_handlers.HandlerForClass(stream, claxx).ClassReflector();
			}
			com.db4o.YapClass yc = stream.GetYapClass(claxx, true);
			if (yc != null)
			{
				yapClassID = yc.GetID();
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
			a_bytes.WriteInt(-yapClassID);
		}

		public override void WriteIndexEntry(com.db4o.YapReader a_writer, object a_object
			)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public sealed override object WriteNew(com.db4o.inside.marshall.MarshallerFamily 
			mf, object a_object, bool topLevel, com.db4o.YapWriter a_bytes, bool withIndirection
			, bool restoreLinkOffset)
		{
			return mf._array.WriteNew(this, a_object, restoreLinkOffset, a_bytes);
		}

		public virtual void WriteNew1(object obj, com.db4o.YapWriter writer, int length)
		{
			WriteClass(obj, writer);
			int elements = _reflectArray.GetLength(obj);
			writer.WriteInt(elements);
			if (!i_handler.WriteArray(obj, writer))
			{
				for (int i = 0; i < elements; i++)
				{
					i_handler.WriteNew(com.db4o.inside.marshall.MarshallerFamily.Current(), _reflectArray
						.Get(obj, i), false, writer, true, true);
				}
			}
		}

		public override com.db4o.YapComparable PrepareComparison(object obj)
		{
			i_handler.PrepareComparison(obj);
			return this;
		}

		public override object Current()
		{
			return i_handler.Current();
		}

		public override int CompareTo(object a_obj)
		{
			return -1;
		}

		public override bool IsEqual(object obj)
		{
			if (obj == null)
			{
				return false;
			}
			object[] compareWith = AllElements(obj);
			for (int j = 0; j < compareWith.Length; j++)
			{
				if (i_handler.IsEqual(compareWith[j]))
				{
					return true;
				}
			}
			return false;
		}

		public override bool IsGreater(object obj)
		{
			object[] compareWith = AllElements(obj);
			for (int j = 0; j < compareWith.Length; j++)
			{
				if (i_handler.IsGreater(compareWith[j]))
				{
					return true;
				}
			}
			return false;
		}

		public override bool IsSmaller(object obj)
		{
			object[] compareWith = AllElements(obj);
			for (int j = 0; j < compareWith.Length; j++)
			{
				if (i_handler.IsSmaller(compareWith[j]))
				{
					return true;
				}
			}
			return false;
		}

		public override bool SupportsIndex()
		{
			return false;
		}
	}
}
