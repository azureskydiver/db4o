namespace com.db4o
{
	/// <summary>marker interface for special db4o datatypes</summary>
	internal interface Db4oTypeImpl : com.db4o.TransactionAware
	{
		int adjustReadDepth(int a_depth);

		bool canBind();

		object createDefault(com.db4o.Transaction a_trans);

		bool hasClassIndex();

		void replicateFrom(object obj);

		void setYapObject(com.db4o.YapObject a_yapObject);

		object storedTo(com.db4o.Transaction a_trans);

		void preDeactivate();
	}
}
