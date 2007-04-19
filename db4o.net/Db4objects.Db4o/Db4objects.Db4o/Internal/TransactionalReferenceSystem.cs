using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;

namespace Db4objects.Db4o.Internal
{
	/// <exclude></exclude>
	public class TransactionalReferenceSystem : IReferenceSystem
	{
		internal readonly IReferenceSystem _committedReferences = new HashcodeReferenceSystem
			();

		private IReferenceSystem _newReferences;

		public TransactionalReferenceSystem()
		{
			CreateNewReferences();
		}

		public virtual void AddExistingReference(ObjectReference @ref)
		{
			_committedReferences.AddExistingReference(@ref);
		}

		public virtual void AddExistingReferenceToIdTree(ObjectReference @ref)
		{
			_committedReferences.AddExistingReferenceToIdTree(@ref);
		}

		public virtual void AddExistingReferenceToObjectTree(ObjectReference @ref)
		{
			_committedReferences.AddExistingReferenceToObjectTree(@ref);
		}

		public virtual void AddNewReference(ObjectReference @ref)
		{
			_newReferences.AddNewReference(@ref);
		}

		public virtual void Commit()
		{
			TraveseNewReferences(new _AnonymousInnerClass38(this));
			CreateNewReferences();
		}

		private sealed class _AnonymousInnerClass38 : IVisitor4
		{
			public _AnonymousInnerClass38(TransactionalReferenceSystem _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				this._enclosing._committedReferences.AddExistingReference((ObjectReference)obj);
			}

			private readonly TransactionalReferenceSystem _enclosing;
		}

		public virtual void TraveseNewReferences(IVisitor4 visitor)
		{
			_newReferences.TraverseReferences(visitor);
		}

		private void CreateNewReferences()
		{
			_newReferences = new HashcodeReferenceSystem();
		}

		public virtual ObjectReference ReferenceForId(int id)
		{
			ObjectReference @ref = _newReferences.ReferenceForId(id);
			if (@ref != null)
			{
				return @ref;
			}
			return _committedReferences.ReferenceForId(id);
		}

		public virtual ObjectReference ReferenceForObject(object obj)
		{
			ObjectReference @ref = _newReferences.ReferenceForObject(obj);
			if (@ref != null)
			{
				return @ref;
			}
			return _committedReferences.ReferenceForObject(obj);
		}

		public virtual void RemoveReference(ObjectReference @ref)
		{
			_newReferences.RemoveReference(@ref);
			_committedReferences.RemoveReference(@ref);
		}

		public virtual void Rollback()
		{
			CreateNewReferences();
		}

		public virtual void TraverseReferences(IVisitor4 visitor)
		{
			TraveseNewReferences(visitor);
			_committedReferences.TraverseReferences(visitor);
		}
	}
}
