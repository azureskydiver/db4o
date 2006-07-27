/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

import com.db4o.query.Query;

public interface QueryEventArgs extends EventArgs {
	Query subject();
}
