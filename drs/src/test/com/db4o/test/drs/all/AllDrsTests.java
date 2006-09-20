package com.db4o.test.drs.all;

import com.db4o.test.drs.Db4oTests;
import com.db4o.test.drs.hibernate.RdbmsTests;

public class AllDrsTests {
	public static void main(String[] args) {
		Db4oTests.main(args);
		RdbmsTests.main(args);
	}
}
