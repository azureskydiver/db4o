/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.hibernate;

import org.hibernate.cfg.*;

import com.db4o.inside.replication.*;
import com.db4o.replication.hibernate.*;
import com.db4o.test.replication.collections.*;

public class HibernateListTest extends ListTest{

    protected TestableReplicationProvider prepareProviderA() {
        Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
        configuration.addClass(ListHolder.class);
        configuration.addClass(ListContent.class);
        HibernateReplicationProviderImpl p = new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
        return p;
    }

    protected TestableReplicationProvider prepareProviderB() {
        Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
        configuration.addClass(ListHolder.class);
        configuration.addClass(ListContent.class);
        HibernateReplicationProviderImpl p = new HibernateReplicationProviderImpl(configuration, "B", new byte[]{2});
        return p;
    }

    public void test() {
        super.test();
    }
    
}
