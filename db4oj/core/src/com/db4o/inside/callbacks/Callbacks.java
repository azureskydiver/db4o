package com.db4o.inside.callbacks;

import com.db4o.query.Query;

public interface Callbacks {

	void onQueryStarted(Query query);
	void onQueryFinished(Query query);

	boolean objectCanNew(Object obj);
	boolean objectCanActivate(Object obj);
	boolean objectCanUpdate(Object obj);
	boolean objectCanDelete(Object obj);

	void objectOnActivate(Object obj);
	void objectOnNew(Object obj);
	void objectOnUpdate(Object obj);
	void objectOnDelete(Object obj);
}
