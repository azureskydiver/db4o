/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.transients;

import com.db4o.inside.replication.*;
import com.db4o.replication.db4o.*;
import com.db4o.test.*;
import com.db4o.test.replication.*;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;


public class TransientSimpleParentChild extends SimpleParentChild {

    protected TestableReplicationProvider prepareProviderA() {
        return new TransientReplicationProvider(new byte[]{1},"A");
    }

    protected TestableReplicationProvider prepareProviderB() {
	    return new TransientReplicationProvider(new byte[]{2},"B");
    }

    public void test() {
        super.test();
    }

}
