namespace com.db4o
{
	/// <exclude></exclude>
	public class YapClassPrimitive : com.db4o.YapClass
	{
		public readonly com.db4o.TypeHandler4 i_handler;

		internal YapClassPrimitive(com.db4o.YapStream a_stream, com.db4o.TypeHandler4 a_handler
			) : base(a_stream, a_handler.ClassReflector())
		{
			i_fields = com.db4o.YapField.EMPTY_ARRAY;
			i_handler = a_handler;
		}

		internal override void ActivateFields(com.db4o.Transaction a_trans, object a_object
			, int a_depth)
		{
		}

		internal sealed override void AddToIndex(com.db4o.YapFile a_stream, com.db4o.Transaction
			 a_trans, int a_id)
		{
		}

		internal override bool AllowsQueries()
		{
			return false;
		}

		internal override void CacheDirty(com.db4o.foundation.Collection4 col)
		{
		}

		public override bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			return i_handler.CanHold(claxx);
		}

		public override com.db4o.reflect.ReflectClass ClassReflector()
		{
			return i_handler.ClassReflector();
		}

		public override void DeleteEmbedded(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapWriter a_bytes)
		{
			if (mf._primitive.UseNormalClassRead())
			{
				base.DeleteEmbedded(mf, a_bytes);
				return;
			}
		}

		public override void DeleteEmbedded1(com.db4o.inside.marshall.MarshallerFamily mf
			, com.db4o.YapWriter a_bytes, int a_id)
		{
			if (i_handler is com.db4o.YapArray)
			{
				com.db4o.YapArray ya = (com.db4o.YapArray)i_handler;
				if (ya.i_isPrimitive)
				{
					ya.DeletePrimitiveEmbedded(a_bytes, this);
					a_bytes.SlotDelete();
					return;
				}
			}
			if (i_handler is com.db4o.YapClassAny)
			{
				a_bytes.IncrementOffset(i_handler.LinkLength());
			}
			else
			{
				i_handler.DeleteEmbedded(mf, a_bytes);
			}
			Free(a_bytes, a_id);
		}

		internal override void DeleteMembers(com.db4o.inside.marshall.MarshallerFamily mf
			, com.db4o.inside.marshall.ObjectHeaderAttributes attributes, com.db4o.YapWriter
			 a_bytes, int a_type, bool isUpdate)
		{
			if (a_type == com.db4o.YapConst.TYPE_ARRAY)
			{
				new com.db4o.YapArray(a_bytes.GetStream(), this, true).DeletePrimitiveEmbedded(a_bytes
					, this);
			}
			else
			{
				if (a_type == com.db4o.YapConst.TYPE_NARRAY)
				{
					new com.db4o.YapArrayN(a_bytes.GetStream(), this, true).DeletePrimitiveEmbedded(a_bytes
						, this);
				}
			}
		}

		internal void Free(com.db4o.Transaction a_trans, int a_id, int a_address, int a_length
			)
		{
			a_trans.SlotFreePointerOnCommit(a_id, a_address, a_length);
		}

		internal void Free(com.db4o.YapWriter a_bytes, int a_id)
		{
			a_bytes.GetTransaction().SlotFreePointerOnCommit(a_id, a_bytes.GetAddress(), a_bytes
				.GetLength());
		}

		public override bool HasIndex()
		{
			return false;
		}

		internal override object Instantiate(com.db4o.YapObject a_yapObject, object a_object
			, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter a_bytes, bool a_addToIDTree)
		{
			if (a_object == null)
			{
				try
				{
					a_object = i_handler.Read(mf, a_bytes, true);
				}
				catch (com.db4o.CorruptionException)
				{
					return null;
				}
				a_yapObject.SetObjectWeak(a_bytes.GetStream(), a_object);
			}
			a_yapObject.SetStateClean();
			return a_object;
		}

		internal override object InstantiateTransient(com.db4o.YapObject a_yapObject, object
			 a_object, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter a_bytes)
		{
			try
			{
				return i_handler.Read(mf, a_bytes, true);
			}
			catch (com.db4o.CorruptionException)
			{
				return null;
			}
		}

		internal override void InstantiateFields(com.db4o.YapObject a_yapObject, object a_onObject
			, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter a_bytes)
		{
			object obj = null;
			try
			{
				obj = i_handler.Read(mf, a_bytes, true);
			}
			catch (com.db4o.CorruptionException)
			{
			}
			if (obj != null)
			{
				i_handler.CopyValue(obj, a_onObject);
			}
		}

		public override bool IsArray()
		{
			return i_id == com.db4o.YapHandlers.ANY_ARRAY_ID || i_id == com.db4o.YapHandlers.
				ANY_ARRAY_N_ID;
		}

		public override bool IsPrimitive()
		{
			return true;
		}

		public override int IsSecondClass()
		{
			return com.db4o.YapConst.UNKNOWN;
		}

		internal override bool IsStrongTyped()
		{
			return false;
		}

		public override void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
			i_handler.CalculateLengths(trans, header, topLevel, obj, withIndirection);
		}

		public override com.db4o.YapComparable PrepareComparison(object a_constraint)
		{
			i_handler.PrepareComparison(a_constraint);
			return i_handler;
		}

		public sealed override com.db4o.reflect.ReflectClass PrimitiveClassReflector()
		{
			return i_handler.PrimitiveClassReflector();
		}

		public override object Read(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter
			 a_bytes, bool redirect)
		{
			if (mf._primitive.UseNormalClassRead())
			{
				return base.Read(mf, a_bytes, redirect);
			}
			return i_handler.Read(mf, a_bytes, false);
		}

		public override com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction a_trans
			, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapReader[] a_bytes)
		{
			if (IsArray())
			{
				return i_handler;
			}
			return null;
		}

		public override object ReadQuery(com.db4o.Transaction trans, com.db4o.inside.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.YapReader reader, bool toArray)
		{
			if (mf._primitive.UseNormalClassRead())
			{
				return base.ReadQuery(trans, mf, withRedirection, reader, toArray);
			}
			return i_handler.ReadQuery(trans, mf, withRedirection, reader, toArray);
		}

		public override com.db4o.QCandidate ReadSubCandidate(com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapReader reader, com.db4o.QCandidates candidates, bool withIndirection
			)
		{
			return i_handler.ReadSubCandidate(mf, reader, candidates, withIndirection);
		}

		internal override void RemoveFromIndex(com.db4o.Transaction ta, int id)
		{
		}

		public override bool SupportsIndex()
		{
			return true;
		}

		public sealed override bool WriteObjectBegin()
		{
			return false;
		}

		public override object WriteNew(com.db4o.inside.marshall.MarshallerFamily mf, object
			 a_object, bool topLevel, com.db4o.YapWriter a_bytes, bool withIndirection, bool
			 restoreLinkOffset)
		{
			mf._primitive.WriteNew(a_bytes.GetTransaction(), this, a_object, topLevel, a_bytes
				, withIndirection, restoreLinkOffset);
			return a_object;
		}

		public override string ToString()
		{
			return "Wraps " + i_handler.ToString() + " in YapClassPrimitive";
		}

		public override void Defrag(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.ReaderPair
			 readers, bool redirect)
		{
			if (mf._primitive.UseNormalClassRead())
			{
				base.Defrag(mf, readers, redirect);
			}
			else
			{
				i_handler.Defrag(mf, readers, false);
			}
		}
	}
}
