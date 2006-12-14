/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.defragment.*;
import com.db4o.query.*;

import db4ounit.*;

public class SlotDefragmentTestCase implements TestLifeCycle {
	
	public void testPrimitiveIndex() throws Exception {
		SlotDefragmentFixture.assertIndex(SlotDefragmentFixture.PRIMITIVE_FIELDNAME);
	}

	public void testWrapperIndex() throws Exception {
		SlotDefragmentFixture.assertIndex(SlotDefragmentFixture.WRAPPER_FIELDNAME);
	}

	public void testTypedObjectIndex() throws Exception {
		SlotDefragmentFixture.forceIndex();
		Defragment.defrag(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),SlotDefragmentConstants.FILENAME);
		Query query=db.query();
		query.constrain(SlotDefragmentFixture.Data.class);
		query.descend(SlotDefragmentFixture.TYPEDOBJECT_FIELDNAME).descend(SlotDefragmentFixture.PRIMITIVE_FIELDNAME).constrain(new Integer(SlotDefragmentFixture.VALUE));
		ObjectSet result=query.execute();
		Assert.areEqual(1,result.size());
		db.close();
	}

	public void testNoForceDelete() throws Exception {
		Defragment.defrag(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
		Assert.expect(IOException.class, new CodeBlock() {
			public void run() throws Exception {
				Defragment.defrag(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
			}
		});
	}	

	public void setUp() throws Exception {
		new File(SlotDefragmentConstants.FILENAME).delete();
		new File(SlotDefragmentConstants.BACKUPFILENAME).delete();
		SlotDefragmentFixture.createFile(SlotDefragmentConstants.FILENAME);
	}

	public void tearDown() throws Exception {
	}
}
