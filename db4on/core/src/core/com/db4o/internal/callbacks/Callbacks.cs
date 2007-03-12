namespace com.db4o.@internal.callbacks
{
	public interface Callbacks
	{
		bool ObjectCanNew(object obj);

		bool ObjectCanActivate(object obj);

		bool ObjectCanUpdate(object obj);

		bool ObjectCanDelete(object obj);

		bool ObjectCanDeactivate(object obj);

		void ObjectOnActivate(object obj);

		void ObjectOnNew(object obj);

		void ObjectOnUpdate(object obj);

		void ObjectOnDelete(object obj);

		void ObjectOnDeactivate(object obj);

		void OnQueryStarted(com.db4o.query.Query query);

		void OnQueryFinished(com.db4o.query.Query query);

		void CommitOnStarted(com.db4o.ext.ObjectInfoCollection added, com.db4o.ext.ObjectInfoCollection
			 deleted, com.db4o.ext.ObjectInfoCollection updated);

		bool CaresAboutCommit();
	}
}
