/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import java.io.*;

import com.db4o.internal.*;

import db4ounit.extensions.*;


public abstract class FileSizeTestCaseBase
	extends AbstractDb4oTestCase
	implements OptOutTA {
    
    protected int databaseFileSize() {
        LocalObjectContainer localContainer = fixture().fileSession();
        localContainer.syncFiles();
        long length = new File(localContainer.fileName()).length();
        return (int)length;
    }
    
}
