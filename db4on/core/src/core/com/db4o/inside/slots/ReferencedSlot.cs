namespace com.db4o.inside.slots
{
	/// <exclude></exclude>
	public class ReferencedSlot : com.db4o.TreeInt
	{
		private com.db4o.inside.slots.Slot _slot;

		private int _references;

		public ReferencedSlot(int a_key) : base(a_key)
		{
		}

		public override object ShallowClone()
		{
			com.db4o.inside.slots.ReferencedSlot rs = new com.db4o.inside.slots.ReferencedSlot
				(_key);
			rs._slot = _slot;
			rs._references = _references;
			return base.ShallowCloneInternal(rs);
		}

		public virtual void PointTo(com.db4o.inside.slots.Slot slot)
		{
			_slot = slot;
		}

		public virtual com.db4o.foundation.Tree Free(com.db4o.YapFile file, com.db4o.foundation.Tree
			 treeRoot, com.db4o.inside.slots.Slot slot)
		{
			file.Free(_slot._address, _slot._length);
			if (RemoveReferenceIsLast())
			{
				return treeRoot.RemoveNode(this);
			}
			PointTo(slot);
			return treeRoot;
		}

		public virtual bool AddReferenceIsFirst()
		{
			_references++;
			return (_references == 1);
		}

		public virtual bool RemoveReferenceIsLast()
		{
			_references--;
			return _references < 1;
		}

		public virtual com.db4o.inside.slots.Slot Slot()
		{
			return _slot;
		}
	}
}
