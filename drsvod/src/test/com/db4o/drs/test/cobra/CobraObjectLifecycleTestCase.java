/* Copyright (C) 2004 - 2011  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.cobra;


import com.db4o.drs.versant.*;
import static com.db4o.qlin.QLinSupport.*;

import db4ounit.*;

public class CobraObjectLifecycleTestCase implements TestCase {
	
	public void test(){
		VodDatabase vod = new VodDatabase("drscobra", "drs", "drs");
		vod.removeDb();
		vod.produceDb();
		vod.addUser();
		VodCobraFacade cobra = VodCobra.createInstance(vod);
		cobra.produceSchema(Item.class);
		Item item = new Item("one");
		item.setLongs(new long[] {1, 2});
		cobra.store(item);
		cobra.commit();
		
		Item i = prototype(Item.class);
		Item retrievedItem = cobra.from(Item.class).where(i.getName()).equal("one").single();
		
		Assert.areEqual("one", retrievedItem.getName());
		ArrayAssert.areEqual(new long[]{1,2}, retrievedItem.getLongs());
		
		cobra.delete(retrievedItem);
		cobra.commit();
		
		cobra.close();
	}

}
