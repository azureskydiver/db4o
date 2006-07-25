package com.db4o.events;

import com.db4o.query.Query;

public interface QueryEventArgs extends EventArgs {
	Query subject();
}
