/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.test.acid.*;


public class CrashSimulatingTestStarter extends AllTests{

    public static void main(String[] args) {
        // Db4o.configure().flushFileBuffers(false);
        runSolo(CrashSimulatingTest.class);
    }

}
