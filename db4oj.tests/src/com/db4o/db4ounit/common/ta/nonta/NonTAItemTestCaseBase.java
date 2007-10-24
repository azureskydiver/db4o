/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.nonta;

import com.db4o.db4ounit.common.ta.*;

public abstract class NonTAItemTestCaseBase extends ItemTestCaseBase {
    
    protected void assertRetrievedItem(Object obj) {
        //do nothing for non-TA tests
        return;
    }
}
