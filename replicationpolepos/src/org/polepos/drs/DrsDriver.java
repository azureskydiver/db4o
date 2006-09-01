/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package org.polepos.drs;


import org.polepos.framework.*;

import com.db4o.*;
import com.db4o.inside.replication.*;
import com.db4o.replication.*;
import com.db4o.test.replication.db4ounit.*;


/**
 * @exclude
 */
public class DrsDriver extends Driver {
    
    private DrsCar _car;
    
    @Override
    public void takeSeatIn(Car car, TurnSetup setup) throws CarMotorFailureException {
        super.takeSeatIn(car, setup);
        _car = (DrsCar)car;
        _car.clean();
    }
    
    public TestableReplicationProviderInside providerA(){
        return fixtureA().provider();
    }

    private DrsFixture fixtureA() {
        return _car.fixtureA();
    }
    
    public TestableReplicationProviderInside providerB(){
        return fixtureB().provider();
    }

    private DrsFixture fixtureB() {
        return _car.fixtureB();
    }


    @Override
    public void prepare() throws CarMotorFailureException {
        try {
            fixtureA().open();
            fixtureB().open();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void backToPit() {
        try {
            fixtureA().close();
            fixtureB().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void replicateAll() {
        ReplicationSession replication = Replication.begin(providerA(), providerB());
        ObjectSet allObjects = providerA().objectsChangedSinceLastReplication();
        while (allObjects.hasNext()) {
            Object changed = allObjects.next();
            if(changed instanceof CheckSummable){
                addToCheckSum(((CheckSummable)changed).checkSum());
            }
            replication.replicate(changed);
        }
        replication.commit();
    }

}
