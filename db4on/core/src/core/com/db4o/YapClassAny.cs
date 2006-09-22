namespace com.db4o
{
	/// <summary>Undefined YapClass used for members of type Object.</summary>
	/// <remarks>Undefined YapClass used for members of type Object.</remarks>
	internal sealed class YapClassAny : com.db4o.YapClass
	{
		public YapClassAny(com.db4o.YapStream stream) : base(stream, stream.i_handlers.ICLASS_OBJECT
			)
		{
		}

		public override bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			return true;
		}

		public override void CascadeActivation(com.db4o.Transaction a_trans, object a_object
			, int a_depth, bool a_activate)
		{
			com.db4o.YapClass yc = ForObject(a_trans, a_object, false);
			if (yc != null)
			{
				yc.CascadeActivation(a_trans, a_object, a_depth, a_activate);
			}
		}

		public override void DeleteEmbedded(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapWriter reader)
		{
			mf._untyped.DeleteEmbedded(reader);
		}

		public override int GetID()
		{
			return 11;
		}

		public override bool HasField(com.db4o.YapStream a_stream, string a_path)
		{
			return a_stream.ClassCollection().FieldExists(a_path);
		}

		internal override bool HasIndex()
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
			return com.db4o.YapConst.UNKNOWN;
		}

		internal override bool IsStrongTyped()
		{
			return false;
		}

		public override void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
			if (topLevel)
			{
				header.AddBaseLength(com.db4o.YapConst.INT_LENGTH);
			}
			else
			{
				header.AddPayLoadLength(com.db4o.YapConst.INT_LENGTH);
			}
			com.db4o.YapClass yc = ForObject(trans, obj, true);
			if (yc == null)
			{
				return;
			}
			header.AddPayLoadLength(com.db4o.YapConst.INT_LENGTH);
			yc.CalculateLengths(trans, header, false, obj, false);
		}

		public override object Read(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter
			 a_bytes, bool redirect)
		{
			if (mf._untyped.UseNormalClassRead())
			{
				return base.Read(mf, a_bytes, redirect);
			}
			return mf._untyped.Read(a_bytes);
		}

		public override com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction a_trans
			, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapReader[] a_bytes)
		{
			return mf._untyped.ReadArrayHandler(a_trans, a_bytes);
		}

		public override object ReadQuery(com.db4o.Transaction trans, com.db4o.inside.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.YapReader reader, bool toArray)
		{
			if (mf._untyped.UseNormalClassRead())
			{
				return base.ReadQuery(trans, mf, withRedirection, reader, toArray);
			}
			return mf._untyped.ReadQuery(trans, reader, toArray);
		}

		public override com.db4o.QCandidate ReadSubCandidate(com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapReader reader, com.db4o.QCandidates candidates, bool withIndirection
			)
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

		public override object WriteNew(com.db4o.inside.marshall.MarshallerFamily mf, object
			 obj, bool topLevel, com.db4o.YapWriter writer, bool withIndirection, bool restoreLinkeOffset
			)
		{
			return mf._untyped.WriteNew(obj, restoreLinkeOffset, writer);
		}

		public override void Defrag(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapReader
			 source, com.db4o.YapReader target, com.db4o.IDMapping mapping)
		{
			int linkLength = LinkLength();
			source.IncrementOffset(linkLength);
			target.IncrementOffset(linkLength);
		}
	}
}
