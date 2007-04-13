/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */
package db4ounit.extensions;

import db4ounit.*;

public final class Db4oFixtureLabelProvider {

	public static final LabelProvider DEFAULT = new LabelProvider() {

		public String getLabel(TestMethod method) {
			return "[" + fixtureLabel(method) + "] "
					+ TestMethod.DEFAULT_LABEL_PROVIDER.getLabel(method);
		}

		private String fixtureLabel(TestMethod method) {
			return ((AbstractDb4oTestCase) method.getSubject()).fixture()
					.getLabel();
		}
	};
}
