/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.fileheader;

import com.db4o.test.*;
import com.db4o.test.replication.old.R0to4Runner;


public class FileHeaderTestSuite extends TestSuite{

    @Override
    public Class[] tests() {
        return new Class[] {
            R0to4Runner.class,
            SimplestPossible.class,
            UpdatingDb4oVersions.class
        };
    }

}
