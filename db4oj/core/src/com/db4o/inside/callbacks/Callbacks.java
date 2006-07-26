package com.db4o.inside.callbacks;

import com.db4o.query.Query;

public interface Callbacks {

	void onQueryStarted(Query query);
	
	void onQueryFinished(Query query);

	boolean objectCanNew(Object subject);

	boolean objectCanActivate(Object subject);

	void objectOnActivate(Object obj);
	
}
