/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.inside.*;

/**
 * @exclude
 */
public interface TransactionAware {
	void setTrans(Transaction a_trans);
}
