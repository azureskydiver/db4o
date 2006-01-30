/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o.hibernate;

import org.hibernate.cfg.*;

import com.db4o.inside.replication.*;
import com.db4o.replication.db4o.*;
import com.db4o.replication.hibernate.*;
import com.db4o.test.*;
import com.db4o.test.replication.*;
import com.db4o.test.replication.hibernate.*;


public class Db4oHibernateR0to4Runner extends R0to4Runner{
    
    protected TestableReplicationProvider prepareProviderA() {
        Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
        configuration.addClass(R0.class);
        HibernateReplicationProviderImpl p = new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
        return p;
    }

    protected TestableReplicationProvider prepareProviderB() {
        return new Db4oReplicationProvider(Test.objectContainer());
    }

    public void test() {
        super.test();
    }

}
