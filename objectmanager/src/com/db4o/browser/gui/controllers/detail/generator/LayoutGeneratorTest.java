/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers.detail.generator;

import junit.framework.TestCase;

public class LayoutGeneratorTest extends TestCase {
	public void testResourceFile() {
		String contents = LayoutGenerator.resourceFile("LayoutGeneratorTestFile");
		assertEquals("TestFile", contents);
	}
}
