namespace com.db4o.@internal.freespace
{
	internal class FreespaceIxAddress : com.db4o.@internal.freespace.FreespaceIx
	{
		internal FreespaceIxAddress(com.db4o.@internal.LocalObjectContainer file, com.db4o.MetaIndex
			 metaIndex) : base(file, metaIndex)
		{
		}

		internal override void Add(int address, int length)
		{
			_index._handler.PrepareComparison(address);
			_indexTrans.Add(length, address);
		}

		internal override int Address()
		{
			return _visitor._value;
		}

		internal override int Length()
		{
			return _visitor._key;
		}

		internal override void Remove(int address, int length)
		{
			_index._handler.PrepareComparison(address);
			_indexTrans.Remove(length, address);
		}

		internal virtual int FreeSize()
		{
			com.db4o.foundation.MutableInt mint = new com.db4o.foundation.MutableInt();
			com.db4o.foundation.IntObjectVisitor freespaceVisitor = new _AnonymousInnerClass37
				(this, mint);
			Traverse(new _AnonymousInnerClass42(this, freespaceVisitor));
			return mint.Value();
		}

		private sealed class _AnonymousInnerClass37 : com.db4o.foundation.IntObjectVisitor
		{
			public _AnonymousInnerClass37(FreespaceIxAddress _enclosing, com.db4o.foundation.MutableInt
				 mint)
			{
				this._enclosing = _enclosing;
				this.mint = mint;
			}

			public void Visit(int anInt, object anObject)
			{
				mint.Add(anInt);
			}

			private readonly FreespaceIxAddress _enclosing;

			private readonly com.db4o.foundation.MutableInt mint;
		}

		private sealed class _AnonymousInnerClass42 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass42(FreespaceIxAddress _enclosing, com.db4o.foundation.IntObjectVisitor
				 freespaceVisitor)
			{
				this._enclosing = _enclosing;
				this.freespaceVisitor = freespaceVisitor;
			}

			public void Visit(object obj)
			{
				((com.db4o.@internal.ix.IxTree)obj).VisitAll(freespaceVisitor);
			}

			private readonly FreespaceIxAddress _enclosing;

			private readonly com.db4o.foundation.IntObjectVisitor freespaceVisitor;
		}
	}
}
