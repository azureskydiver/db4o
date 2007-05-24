/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.defragment;

import java.io.*;

import com.db4o.db4ounit.common.defragment.*;
import com.db4o.defragment.*;
import com.db4o.foundation.*;
import com.db4o.test.util.*;

import db4ounit.*;

/**
 * This one tests common, non-jdk1.2 specific functionality, but requires an
 * ExcludingClassLoader which doesn't work on JDK < 1.2.
 */
public class DefragmentSkipClassTestCase implements TestLifeCycle {

	public void testSkipsClass() throws Exception {
		DefragmentConfig defragConfig = SlotDefragmentFixture.defragConfig(true);
		Defragment.defrag(defragConfig);
		SlotDefragmentFixture.assertDataClassKnown(true);

		defragConfig = SlotDefragmentFixture.defragConfig(true);
		defragConfig.storedClassFilter(new AvailableClassFilter(SlotDefragmentFixture.Data.class.getClassLoader()));
		Defragment.defrag(defragConfig);
		SlotDefragmentFixture.assertDataClassKnown(true);

		defragConfig = SlotDefragmentFixture.defragConfig(true);
		Collection4 excluded=new Collection4();
		excluded.add(SlotDefragmentFixture.Data.class.getName());
		ExcludingClassLoader loader=new ExcludingClassLoader(SlotDefragmentFixture.Data.class.getClassLoader(),excluded);
		defragConfig.storedClassFilter(new AvailableClassFilter(loader));
		Defragment.defrag(defragConfig);
		SlotDefragmentFixture.assertDataClassKnown(false);
	}

	public void setUp() throws Exception {
		new File(SlotDefragmentTestConstants.FILENAME).delete();
		new File(SlotDefragmentTestConstants.BACKUPFILENAME).delete();
		SlotDefragmentFixture.createFile(SlotDefragmentTestConstants.FILENAME);
	}

	public void tearDown() throws Exception {
	}
}
