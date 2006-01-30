/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

/**
 * Exception to be thrown on conflicts (Object changed in
 * both replication providers) in a {@link ReplicationSession}
 * where no {@link ConflictResolver} is
 * used.
 */

public class ReplicationConflictException extends RuntimeException {


}
