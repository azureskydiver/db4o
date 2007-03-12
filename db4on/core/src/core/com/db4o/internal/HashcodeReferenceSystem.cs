namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class HashcodeReferenceSystem : com.db4o.@internal.ReferenceSystem
	{
		private com.db4o.@internal.ObjectReference _hashCodeTree;

		private com.db4o.@internal.ObjectReference _idTree;

		public virtual void AddNewReference(com.db4o.@internal.ObjectReference @ref)
		{
			AddReference(@ref);
		}

		public virtual void AddExistingReference(com.db4o.@internal.ObjectReference @ref)
		{
			AddReference(@ref);
		}

		private void AddReference(com.db4o.@internal.ObjectReference @ref)
		{
			IdAdd(@ref);
			HashCodeAdd(@ref);
		}

		public virtual void AddExistingReferenceToObjectTree(com.db4o.@internal.ObjectReference
			 @ref)
		{
			HashCodeAdd(@ref);
		}

		public virtual void AddExistingReferenceToIdTree(com.db4o.@internal.ObjectReference
			 @ref)
		{
			IdAdd(@ref);
		}

		public virtual void Commit()
		{
		}

		private void HashCodeAdd(com.db4o.@internal.ObjectReference @ref)
		{
			if (_hashCodeTree == null)
			{
				@ref.Hc_init();
				_hashCodeTree = @ref;
				return;
			}
			_hashCodeTree = _hashCodeTree.Hc_add(@ref);
		}

		private void IdAdd(com.db4o.@internal.ObjectReference @ref)
		{
			if (_idTree == null)
			{
				@ref.Hc_init();
				_idTree = @ref;
				return;
			}
			_idTree = _idTree.Id_add(@ref);
		}

		public virtual com.db4o.@internal.ObjectReference ReferenceForId(int id)
		{
			if (_idTree == null)
			{
				return null;
			}
			if (!com.db4o.@internal.ObjectReference.IsValidId(id))
			{
				return null;
			}
			return _idTree.Id_find(id);
		}

		public virtual com.db4o.@internal.ObjectReference ReferenceForObject(object obj)
		{
			if (_hashCodeTree == null)
			{
				return null;
			}
			return _hashCodeTree.Hc_find(obj);
		}

		public virtual void RemoveReference(com.db4o.@internal.ObjectReference @ref)
		{
			if (_hashCodeTree != null)
			{
				_hashCodeTree = _hashCodeTree.Hc_remove(@ref);
			}
			if (_idTree != null)
			{
				_idTree = _idTree.Id_remove(@ref.GetID());
			}
		}

		public virtual void Rollback()
		{
		}

		public virtual void TraverseReferences(com.db4o.foundation.Visitor4 visitor)
		{
			if (_hashCodeTree == null)
			{
				return;
			}
			_hashCodeTree.Hc_traverse(visitor);
		}
	}
}
