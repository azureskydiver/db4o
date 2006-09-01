/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package org.polepos.drs;

import org.polepos.circuits.kyalami.*;

public class KyalamiDrs extends DrsDriver implements KyalamiDriver {

    public  void storeInA() {
        int count = setup().getObjectCount();
        for (int i = 0; i < count; i++) {
            KyalamiObject ko = new KyalamiObject(i);
            addToCheckSum(ko.checkSum());
            providerA().storeNew(ko);
        }
        providerA().commit();
    }

    public void replicate() {
        replicateAll();
    }

}