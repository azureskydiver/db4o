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

		public virtual void pointTo(com.db4o.inside.slots.Slot slot)
		{
			_slot = slot;
		}

		public virtual com.db4o.Tree free(com.db4o.YapFile file, com.db4o.Tree treeRoot, 
			com.db4o.inside.slots.Slot slot)
		{
			file.free(_slot._address, _slot._length);
			if (removeReferenceIsLast())
			{
				return treeRoot.removeNode(this);
			}
			pointTo(slot);
			return treeRoot;
		}

		public virtual bool addReferenceIsFirst()
		{
			_references++;
			return (_references == 1);
		}

		public virtual bool removeReferenceIsLast()
		{
			_references--;
			return _references < 1;
		}
	}
}
