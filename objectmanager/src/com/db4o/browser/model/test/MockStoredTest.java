package com.db4o.browser.model.test;

import com.db4o.ext.*;

import junit.framework.*;

public class MockStoredTest extends TestCase {
	private final static Bar bar=new Bar("test",new int[]{42});

	public void testStoredField() throws Exception {
		MockStoredField sfield=new MockStoredField(Foo.class.getDeclaredField("s"));
		assertEquals("s",sfield.getName());
		assertEquals(bar.s(),sfield.get(bar));
		assertFalse(sfield.isArray());
		MockStoredField ifield=new MockStoredField(Bar.class.getDeclaredField("i"));
		assertEquals("i",ifield.getName());
		int[] ivalue=(int[])ifield.get(bar);
		assertEquals(1,ivalue.length);
		assertEquals(bar.i()[0],ivalue[0]);
		assertTrue(ifield.isArray());
	}
	
	public void testStoredClass() throws Exception {
		MockStoredClass clazz=new MockStoredClass(Bar.class);
		assertEquals(Bar.class.getName(),clazz.getName());
		assertEquals(new MockStoredClass(Foo.class),clazz.getParentStoredClass());
		StoredField[] fields=clazz.getStoredFields();
		assertEquals(2,fields.length);
		for (int idx = 0; idx < fields.length; idx++) {
			Object value = fields[idx].get(bar);
			if("s".equals(fields[idx].getName())) {
				assertEquals(bar.s(),value);
			}
			else {
				int[] ivalue=(int[])value;
				assertEquals(1,ivalue.length);
				assertEquals(bar.i()[0],ivalue[0]);
			}
		}
	}
}
