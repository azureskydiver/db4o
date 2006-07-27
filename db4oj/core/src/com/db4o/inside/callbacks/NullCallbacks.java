package com.db4o.inside.callbacks;

import com.db4o.query.Query;

public class NullCallbacks implements Callbacks {

	public void onQueryFinished(Query query) {
	}

	public void onQueryStarted(Query query) {
	}

	public boolean objectCanNew(Object obj) {
		return true;
	}

	public boolean objectCanActivate(Object obj) {
		return true;
	}

	public void objectOnActivate(Object obj) {
	}

	public void objectOnNew(Object obj) {
	}
}
