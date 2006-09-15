package com.db4o.db4ounit.marshall;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class YapStringTestCase extends AbstractDb4oTestCase {

	public void testIndexMarshalling() {
		YapReader reader=new YapReader(2*YapConst.INT_LENGTH);
		YapStream stream=(YapStream)db();
		YapString handler=new YapString(stream,stream.stringIO());
		final int[] original = new int[]{0xdb,0x40};
		handler.writeIndexEntry(reader,original);
		reader._offset=0;
		int[] retrieved=(int[])handler.readIndexEntry(reader);
		ArrayAssert.areEqual(original,retrieved);
	}
	
}
