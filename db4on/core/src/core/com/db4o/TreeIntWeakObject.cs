namespace com.db4o
{
	/// <exclude></exclude>
	public class TreeIntWeakObject : com.db4o.TreeIntObject
	{
		public TreeIntWeakObject(int key) : base(key)
		{
		}

		public TreeIntWeakObject(int key, object obj) : base(key, com.db4o.Platform4.createWeakReference
			(obj))
		{
		}

		public override object shallowClone()
		{
			return shallowCloneInternal(new com.db4o.TreeIntWeakObject(_key));
		}

		protected override com.db4o.Tree shallowCloneInternal(com.db4o.Tree tree)
		{
			com.db4o.TreeIntWeakObject tiwo = (com.db4o.TreeIntWeakObject)base.shallowCloneInternal
				(tree);
			tiwo.setObject(getObject());
			return tiwo;
		}

		public virtual object getObject()
		{
			return com.db4o.Platform4.weakReferenceTarget(_object);
		}

		public virtual void setObject(object obj)
		{
			_object = com.db4o.Platform4.createWeakReference(obj);
		}

		public com.db4o.TreeIntWeakObject traverseRemoveEmpty(com.db4o.foundation.Visitor4
			 visitor)
		{
			if (_preceding != null)
			{
				_preceding = ((com.db4o.TreeIntWeakObject)_preceding).traverseRemoveEmpty(visitor
					);
			}
			if (_subsequent != null)
			{
				_subsequent = ((com.db4o.TreeIntWeakObject)_subsequent).traverseRemoveEmpty(visitor
					);
			}
			object referent = com.db4o.Platform4.weakReferenceTarget(_object);
			if (referent == null)
			{
				return (com.db4o.TreeIntWeakObject)remove();
			}
			visitor.visit(referent);
			calculateSize();
			return this;
		}
	}
}
