/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.fileheader;

import com.db4o.test.*;



/**
 */
@decaf.Ignore
public class FileHeaderTestSuite extends TestSuite{

    @Override
    public Class[] tests() {
        return new Class[] {
            SimplestPossible.class,
        };
    }

}
