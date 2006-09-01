namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class ObjectHeaderAttributes1 : com.db4o.inside.marshall.ObjectHeaderAttributes
	{
		private const byte VERSION = (byte)1;

		private readonly int _fieldCount;

		private readonly com.db4o.foundation.BitMap4 _nullBitMap;

		private int _baseLength;

		private int _payLoadLength;

		public ObjectHeaderAttributes1(com.db4o.YapObject yo)
		{
			_fieldCount = yo.GetYapClass().FieldCount();
			_nullBitMap = new com.db4o.foundation.BitMap4(_fieldCount);
			CalculateLengths(yo);
		}

		public ObjectHeaderAttributes1(com.db4o.YapReader reader)
		{
			_fieldCount = reader.ReadInt();
			_nullBitMap = reader.ReadBitMap(_fieldCount);
		}

		public override void AddBaseLength(int length)
		{
			_baseLength += length;
		}

		public override void AddPayLoadLength(int length)
		{
			_payLoadLength += length;
		}

		private void CalculateLengths(com.db4o.YapObject yo)
		{
			_baseLength = HeaderLength() + NullBitMapLength();
			_payLoadLength = 0;
			com.db4o.YapClass yc = yo.GetYapClass();
			com.db4o.Transaction trans = yo.GetTrans();
			object obj = yo.GetObject();
			CalculateLengths(trans, yc, obj, 0);
			_baseLength = yo.GetStream().AlignToBlockSize(_baseLength);
		}

		private void CalculateLengths(com.db4o.Transaction trans, com.db4o.YapClass yc, object
			 obj, int fieldIndex)
		{
			_baseLength += com.db4o.YapConst.INT_LENGTH;
			if (yc.i_fields != null)
			{
				for (int i = 0; i < yc.i_fields.Length; i++)
				{
					com.db4o.YapField yf = yc.i_fields[i];
					object child = yf.GetOrCreate(trans, obj);
					if (child == null && yf.CanUseNullBitmap())
					{
						_nullBitMap.SetTrue(fieldIndex);
					}
					else
					{
						yf.CalculateLengths(trans, this, child);
					}
					fieldIndex++;
				}
			}
			if (yc.i_ancestor == null)
			{
				return;
			}
			CalculateLengths(trans, yc.i_ancestor, obj, fieldIndex);
		}

		private int HeaderLength()
		{
			return com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst.ID_LENGTH + 1;
		}

		public virtual bool IsNull(int fieldIndex)
		{
			return _nullBitMap.IsTrue(fieldIndex);
		}

		private int NullBitMapLength()
		{
			return com.db4o.YapConst.INT_LENGTH + _nullBitMap.MarshalledLength();
		}

		public virtual int ObjectLength()
		{
			return _baseLength + _payLoadLength;
		}

		public override void PrepareIndexedPayLoadEntry(com.db4o.Transaction trans)
		{
			_payLoadLength = trans.Stream().AlignToBlockSize(_payLoadLength);
		}

		public virtual void Write(com.db4o.YapWriter writer)
		{
			writer.Append(VERSION);
			writer.WriteInt(_fieldCount);
			writer.WriteBitMap(_nullBitMap);
			writer._payloadOffset = _baseLength;
		}
	}
}
