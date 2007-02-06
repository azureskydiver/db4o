namespace com.db4o.@internal.freespace
{
	internal class FreespaceIxLength : com.db4o.@internal.freespace.FreespaceIx
	{
		internal FreespaceIxLength(com.db4o.@internal.LocalObjectContainer file, com.db4o.MetaIndex
			 metaIndex) : base(file, metaIndex)
		{
		}

		internal override void Add(int address, int length)
		{
			_index._handler.PrepareComparison(length);
			_indexTrans.Add(address, length);
		}

		internal override int Address()
		{
			return _visitor._key;
		}

		internal override int Length()
		{
			return _visitor._value;
		}

		internal override void Remove(int address, int length)
		{
			_index._handler.PrepareComparison(length);
			_indexTrans.Remove(address, length);
		}
	}
}
