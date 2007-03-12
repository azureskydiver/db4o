namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class TransactionalReferenceSystem : com.db4o.@internal.ReferenceSystem
	{
		internal readonly com.db4o.@internal.ReferenceSystem _committedReferences = new com.db4o.@internal.HashcodeReferenceSystem
			();

		private com.db4o.@internal.ReferenceSystem _newReferences;

		public TransactionalReferenceSystem()
		{
			CreateNewReferences();
		}

		public virtual void AddExistingReference(com.db4o.@internal.ObjectReference @ref)
		{
			_committedReferences.AddExistingReference(@ref);
		}

		public virtual void AddExistingReferenceToIdTree(com.db4o.@internal.ObjectReference
			 @ref)
		{
			_committedReferences.AddExistingReferenceToIdTree(@ref);
		}

		public virtual void AddExistingReferenceToObjectTree(com.db4o.@internal.ObjectReference
			 @ref)
		{
			_committedReferences.AddExistingReferenceToObjectTree(@ref);
		}

		public virtual void AddNewReference(com.db4o.@internal.ObjectReference @ref)
		{
			_newReferences.AddNewReference(@ref);
		}

		public virtual void Commit()
		{
			TraveseNewReferences(new _AnonymousInnerClass38(this));
			CreateNewReferences();
		}

		private sealed class _AnonymousInnerClass38 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass38(TransactionalReferenceSystem _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				this._enclosing._committedReferences.AddExistingReference((com.db4o.@internal.ObjectReference
					)obj);
			}

			private readonly TransactionalReferenceSystem _enclosing;
		}

		public virtual void TraveseNewReferences(com.db4o.foundation.Visitor4 visitor)
		{
			_newReferences.TraverseReferences(visitor);
		}

		private void CreateNewReferences()
		{
			_newReferences = new com.db4o.@internal.HashcodeReferenceSystem();
		}

		public virtual com.db4o.@internal.ObjectReference ReferenceForId(int id)
		{
			com.db4o.@internal.ObjectReference @ref = _newReferences.ReferenceForId(id);
			if (@ref != null)
			{
				return @ref;
			}
			return _committedReferences.ReferenceForId(id);
		}

		public virtual com.db4o.@internal.ObjectReference ReferenceForObject(object obj)
		{
			com.db4o.@internal.ObjectReference @ref = _newReferences.ReferenceForObject(obj);
			if (@ref != null)
			{
				return @ref;
			}
			return _committedReferences.ReferenceForObject(obj);
		}

		public virtual void RemoveReference(com.db4o.@internal.ObjectReference @ref)
		{
			_newReferences.RemoveReference(@ref);
			_committedReferences.RemoveReference(@ref);
		}

		public virtual void Rollback()
		{
			CreateNewReferences();
		}

		public virtual void TraverseReferences(com.db4o.foundation.Visitor4 visitor)
		{
			TraveseNewReferences(visitor);
			_committedReferences.TraverseReferences(visitor);
		}
	}
}
