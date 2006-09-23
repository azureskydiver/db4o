/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.header;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ConfigurationSettingsTestCase extends AbstractDb4oTestCase {
    
    public void testChangingUuidSettings() throws Exception {
        
        fixture().config().generateUUIDs(0);
        
        reopen();
        
        Assert.areEqual(0, generateUUIDs());        
        
        db().configure().generateUUIDs(-1);
        
        Assert.areEqual(-1, generateUUIDs());
        
        fixture().config().generateUUIDs(0);

        reopen();
        
        Assert.areEqual(0, generateUUIDs());
        
    }

    private int generateUUIDs() {
        return ((YapFile)db()).config().generateUUIDs();
    }
}
