/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.diagnostics;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runAll();
    }

	protected Class[] testCases() {
		return new Class[] {
				ClassHasNoFieldsTestCase.class,
				DescendIntoTranslatorTestCase.class,
				DiagnosticsTestCase.class,
				IndexFieldDiagnosticTestCase.class,
				MissingClassTestCase.class,
		};
	}
}
