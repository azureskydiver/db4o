/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}

	protected Class[] testCases() {
		return new Class[] { 
				ArrayNOrderTestCase.class, 
				ByteArrayTestCase.class,
				CascadeDeleteArrayTestCase.class,
				CascadeDeleteDeletedTestCase.class,
				CascadeDeleteFalseTestCase.class,
				CascadeOnActivateTestCase.class,
				CascadeOnSetTestCase.class,
				CascadeOnUpdateTestCase.class,
				CascadeOnUpdate2TestCase.class,
				CascadeToExistingVectorMemberTestCase.class,
				CascadeToHashtableTestCase.class,
				CascadeToVectorTestCase.class,
				CaseInsensitiveTestCase.class,
				Circular1TestCase.class,
				Circular2TestCase.class,
				ClientDisconnectTestCase.class,
				CloseServerBeforeClientTestCase.class,
				ComparatorSortTestCase.class,
				CreateIndexInheritedTestCase.class,
				CustomActivationDepthTestCase.class,
				DeepSetTestCase.class,
				DeleteDeepTestCase.class,
				DifferentAccessPathsTestCase.class,
				DualDeleteTestCase.class,
				ExtMethodsTestCase.class,
				GetAllTestCase.class,
				HashtableModifiedUpdateDepthTestCase.class,
		};
	}

}
