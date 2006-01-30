/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication.db4o;

import com.db4o.ext.*;
import com.db4o.inside.replication.*;


public class Db4oReplicationProviderSignature implements ReadonlyReplicationProviderSignature {

    private final Db4oDatabase _delegate;

    public Db4oReplicationProviderSignature(Db4oDatabase delegate_) {
        _delegate = delegate_;
    }

    public long getId() {
        return 0;
    }

    public byte[] getBytes() {
        return _delegate.getSignature();
    }

    public long getCreationTime() {
        return _delegate.getCreationTime();
    }

}
