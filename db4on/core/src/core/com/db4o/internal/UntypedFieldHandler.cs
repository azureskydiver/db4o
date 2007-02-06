namespace com.db4o.@internal
{
	public sealed class UntypedFieldHandler : com.db4o.@internal.ClassMetadata
	{
		public UntypedFieldHandler(com.db4o.@internal.ObjectContainerBase stream) : base(
			stream, stream.i_handlers.ICLASS_OBJECT)
		{
		}

		public override bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			return true;
		}

		public override void CascadeActivation(com.db4o.@internal.Transaction a_trans, object
			 a_object, int a_depth, bool a_activate)
		{
			com.db4o.@internal.ClassMetadata yc = ForObject(a_trans, a_object, false);
			if (yc != null)
			{
				yc.CascadeActivation(a_trans, a_object, a_depth, a_activate);
			}
		}

		public override void DeleteEmbedded(com.db4o.@internal.marshall.MarshallerFamily 
			mf, com.db4o.@internal.StatefulBuffer reader)
		{
			mf._untyped.DeleteEmbedded(reader);
		}

		public override int GetID()
		{
			return 11;
		}

		public override bool HasField(com.db4o.@internal.ObjectContainerBase a_stream, string
			 a_path)
		{
			return a_stream.ClassCollection().FieldExists(a_path);
		}

		public override bool HasIndex()
		{
			return false;
		}

		public override bool HasFixedLength()
		{
			return false;
		}

		public override bool HoldsAnyClass()
		{
			return true;
		}

		public override int IsSecondClass()
		{
			return com.db4o.@internal.Const4.UNKNOWN;
		}

		public override bool IsStrongTyped()
		{
			return false;
		}

		public override void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
			if (topLevel)
			{
				header.AddBaseLength(com.db4o.@internal.Const4.INT_LENGTH);
			}
			else
			{
				header.AddPayLoadLength(com.db4o.@internal.Const4.INT_LENGTH);
			}
			com.db4o.@internal.ClassMetadata yc = ForObject(trans, obj, true);
			if (yc == null)
			{
				return;
			}
			header.AddPayLoadLength(com.db4o.@internal.Const4.INT_LENGTH);
			yc.CalculateLengths(trans, header, false, obj, false);
		}

		public override object Read(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes, bool redirect)
		{
			if (mf._untyped.UseNormalClassRead())
			{
				return base.Read(mf, a_bytes, redirect);
			}
			return mf._untyped.Read(a_bytes);
		}

		public override com.db4o.@internal.TypeHandler4 ReadArrayHandler(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.Buffer[]
			 a_bytes)
		{
			return mf._untyped.ReadArrayHandler(a_trans, a_bytes);
		}

		public override object ReadQuery(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.@internal.Buffer reader, bool toArray)
		{
			if (mf._untyped.UseNormalClassRead())
			{
				return base.ReadQuery(trans, mf, withRedirection, reader, toArray);
			}
			return mf._untyped.ReadQuery(trans, reader, toArray);
		}

		public override com.db4o.@internal.query.processor.QCandidate ReadSubCandidate(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.Buffer reader, com.db4o.@internal.query.processor.QCandidates
			 candidates, bool withIndirection)
		{
			if (mf._untyped.UseNormalClassRead())
			{
				return base.ReadSubCandidate(mf, reader, candidates, withIndirection);
			}
			return mf._untyped.ReadSubCandidate(reader, candidates, withIndirection);
		}

		public override bool SupportsIndex()
		{
			return false;
		}

		public override object WriteNew(com.db4o.@internal.marshall.MarshallerFamily mf, 
			object obj, bool topLevel, com.db4o.@internal.StatefulBuffer writer, bool withIndirection
			, bool restoreLinkeOffset)
		{
			return mf._untyped.WriteNew(obj, restoreLinkeOffset, writer);
		}

		public override void Defrag(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.ReaderPair
			 readers, bool redirect)
		{
			if (mf._untyped.UseNormalClassRead())
			{
				base.Defrag(mf, readers, redirect);
			}
			mf._untyped.Defrag(readers);
		}
	}
}
