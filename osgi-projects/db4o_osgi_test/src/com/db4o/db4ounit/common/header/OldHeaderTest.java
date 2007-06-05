/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.db4ounit.common.header;

import java.io.*;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.db4ounit.util.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class OldHeaderTest implements TestCase {
    
    private static final String ORIGINAL_FILE = WorkspaceServices.workspaceTestFilePath("db4oVersions/db4o_5.5.2");
    
    private static final String DB_FILE = WorkspaceServices.workspaceTestFilePath("db4oVersions/db4o_5.5.2.yap");   
    
    public void test() throws IOException {
    	
    	if(! File4.exists(ORIGINAL_FILE)){
    		TestPlatform.emitWarning(ORIGINAL_FILE + " does not exist. Can not run " + getClass().getName());
    		return;
    	}
        
    	File4.copy(ORIGINAL_FILE, DB_FILE);
        
    	Db4o.configure().allowVersionUpdates(true);    	
        ObjectContainer oc = Db4o.openFile(DB_FILE);
        try {
        	Assert.isNotNull(oc);
        } finally {
        	oc.close();
        	Db4o.configure().allowVersionUpdates(false);
        }
    }
}
