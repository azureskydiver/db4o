/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.math.*;

import com.db4o.*;
import com.db4o.query.*;

// TODO This fails on JDK1.3. JDK1.4+ is fine.
public class StoreBigDecimal {
	public BigDecimal _bd;

	public void configure() {
		Db4o.configure().objectClass(BigDecimal.class).callConstructor(true);
		Db4o.configure().objectClass(BigDecimal.class).storeTransientFields(true);
	}
	
	public void store() {
		StoreBigDecimal stored=new StoreBigDecimal();
		stored._bd=new BigDecimal("111.11");
		Test.store(stored);
	}
	
	public void testOne() {
		Query q=Test.query();
		q.constrain(StoreBigDecimal.class);
		ObjectSet r=q.execute();
		Test.ensureEquals(1, r.size());
		StoreBigDecimal stored=(StoreBigDecimal)r.next();
		Test.ensureEquals(new BigDecimal("111.11"),stored._bd);
	}
}
