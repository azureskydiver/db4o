namespace com.db4o.@internal
{
	/// <summary>marker interface for special db4o datatypes</summary>
	/// <exclude></exclude>
	public interface Db4oTypeImpl : com.db4o.TransactionAware
	{
		int AdjustReadDepth(int a_depth);

		bool CanBind();

		object CreateDefault(com.db4o.@internal.Transaction a_trans);

		bool HasClassIndex();

		void ReplicateFrom(object obj);

		void SetYapObject(com.db4o.@internal.ObjectReference a_yapObject);

		object StoredTo(com.db4o.@internal.Transaction a_trans);

		void PreDeactivate();
	}
}
