namespace com.db4o.inside.marshall
{
	public class RawFieldSpec
	{
		private readonly string _name;

		private readonly int _handlerID;

		private readonly bool _isPrimitive;

		private readonly bool _isArray;

		private readonly bool _isNArray;

		private readonly bool _isVirtual;

		private int _indexID;

		public RawFieldSpec(string name, int handlerID, byte attribs)
		{
			_name = name;
			_handlerID = handlerID;
			com.db4o.YapBit yb = new com.db4o.YapBit(attribs);
			_isPrimitive = yb.Get();
			_isArray = yb.Get();
			_isNArray = yb.Get();
			_isVirtual = false;
			_indexID = 0;
		}

		public RawFieldSpec(string name)
		{
			_name = name;
			_handlerID = 0;
			_isPrimitive = false;
			_isArray = false;
			_isNArray = false;
			_isVirtual = true;
			_indexID = 0;
		}

		public virtual string Name()
		{
			return _name;
		}

		public virtual int HandlerID()
		{
			return _handlerID;
		}

		public virtual bool IsPrimitive()
		{
			return _isPrimitive;
		}

		public virtual bool IsArray()
		{
			return _isArray;
		}

		public virtual bool IsNArray()
		{
			return _isNArray;
		}

		public virtual bool IsVirtual()
		{
			return _isVirtual;
		}

		public virtual int IndexID()
		{
			return _indexID;
		}

		internal virtual void IndexID(int indexID)
		{
			_indexID = indexID;
		}
	}
}
