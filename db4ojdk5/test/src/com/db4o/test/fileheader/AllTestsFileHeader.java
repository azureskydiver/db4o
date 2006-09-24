/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.fileheader;

import com.db4o.test.*;

public class AllTestsFileHeader extends AllTestsJdk1_2 {

    public static void main(String[] args) {
        runSolo(new FileHeaderTestSuite());
    }

}
