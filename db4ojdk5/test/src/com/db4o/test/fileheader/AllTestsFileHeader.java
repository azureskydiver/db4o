/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.fileheader;

import com.db4o.db4ounit.common.assorted.SimplestPossibleTestCase;
import com.db4o.test.AllTestsJdk1_2;

public class AllTestsFileHeader extends AllTestsJdk1_2 {

    public static void main(String[] args) {
        runSolo(new FileHeaderTestSuite());
        new SimplestPossibleTestCase().runClientServer();
    }

}
