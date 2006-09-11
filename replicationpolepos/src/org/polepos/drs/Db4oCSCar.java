/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package org.polepos.drs;

import com.db4o.test.replication.db4ounit.fixtures.Db4oClientServerDrsFixture;
import com.db4o.test.replication.db4ounit.fixtures.Db4oDrsFixture;


public class Db4oCSCar extends DrsCar {

    public Db4oCSCar () {
        super(new Db4oDrsFixture("poleposA"), new Db4oClientServerDrsFixture("poleposB", 4445));
    }
    
    @Override
    public String name() {
        return "db4o-cs";
    }

}
