/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

import com.db4o.ext.Db4oException;

public class ReplicationConflictException extends Db4oException {
	public ReplicationConflictException(String message) {
		super(message);
	}
}
