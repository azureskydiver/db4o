namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public interface ReferenceSystem
	{
		void AddNewReference(com.db4o.@internal.ObjectReference @ref);

		void AddExistingReference(com.db4o.@internal.ObjectReference @ref);

		void AddExistingReferenceToObjectTree(com.db4o.@internal.ObjectReference @ref);

		void AddExistingReferenceToIdTree(com.db4o.@internal.ObjectReference @ref);

		void Commit();

		com.db4o.@internal.ObjectReference ReferenceForId(int id);

		com.db4o.@internal.ObjectReference ReferenceForObject(object obj);

		void RemoveReference(com.db4o.@internal.ObjectReference @ref);

		void Rollback();

		void TraverseReferences(com.db4o.foundation.Visitor4 visitor);
	}
}
