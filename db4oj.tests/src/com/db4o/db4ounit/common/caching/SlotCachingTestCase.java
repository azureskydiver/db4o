/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.caching;

import java.util.*;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.caching.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class SlotCachingTestCase extends AbstractDb4oTestCase implements OptOutCS{
	
	public static class Item{
		public Item(int i) {
			_id = 1;
		}
		public int _id;
	}
	
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.cache().slotCacheSize(10);
		config.objectClass(Item.class).objectField("_id").indexed(true);
	}
	
	public void test(){
		store(new Item(1));
		db().commit();
		LocalTransaction systemTrans = (LocalTransaction) systemTrans();
		Cache4<Integer, ByteArrayBuffer> cache = systemTrans.slotCache();
		Assert.isNotNull(cache);
		Iterator<ByteArrayBuffer> i = cache.iterator();
		
		// FIXME: doesn't decaf
//		Assert.isTrue(i.hasNext());
	}

}
