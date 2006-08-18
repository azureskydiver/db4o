/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.other;

import com.db4o.inside.replication.TestableReplicationProviderInside;


class R0Linker {

	R0 r0;
	R1 r1;
	R2 r2;
	R3 r3;
	R4 r4;

	R0Linker() {
		r0 = new R0();
		r1 = new R1();
		r2 = new R2();
		r3 = new R3();
		r4 = new R4();
	}

	void setNames(String name) {
		r0.name = "0" + name;
		r1.name = "1" + name;
		r2.name = "2" + name;
		r3.name = "3" + name;
		r4.name = "4" + name;
	}

	void linkCircles() {
		linkList();
		r1.circle1 = r0;
		r2.circle2 = r0;
		r3.circle3 = r0;
		r4.circle4 = r0;
	}

	void linkList() {
		r0.r1 = r1;
		r1.r2 = r2;
		r2.r3 = r3;
		r3.r4 = r4;
	}

	void linkThis() {
		r0.r0 = r0;
		r1.r1 = r1;
		r2.r2 = r2;
		r3.r3 = r3;
		r4.r4 = r4;
	}

	void linkBack() {
		r1.r0 = r0;
		r2.r1 = r1;
		r3.r2 = r2;
		r4.r3 = r3;
	}

	public void store(TestableReplicationProviderInside provider) {
		provider.storeNew(r4);
		provider.storeNew(r3);
		provider.storeNew(r2);
		provider.storeNew(r1);
		provider.storeNew(r0);
	}
}
