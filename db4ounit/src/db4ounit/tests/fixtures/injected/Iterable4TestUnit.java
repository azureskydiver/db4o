/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures.injected;

import com.db4o.foundation.*;

import db4ounit.*;

public class Iterable4TestUnit extends FixtureSensitiveImpl {
	
	public void testElements() {
		
		Iterable4 subject = fixture().subject;
		Object[] data = fixture().data;
		
		final Iterator4 elements = subject.iterator();
		for (int i=0; i<data.length; ++i) {
			Assert.isTrue(elements.moveNext());
			Assert.areEqual(data[i], elements.current());
		}
		Assert.isFalse(elements.moveNext());
	}
	
	Iterable4Fixture fixture() {
		return (Iterable4Fixture)fixture(Enumerable4FixtureProvider.TOKEN);
	}

}
