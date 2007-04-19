/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency;

import db4ounit.extensions.*;

public class AllTestsSingle extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTestsSingle().runClientServer();
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
				GreaterOrEqualTestCase.class,
				HashtableModifiedUpdateDepthTestCase.class,
				IndexedByIdentityTestCase.class,
				IndexedUpdatesWithNullTestCase.class,
				InternStringsTestCase.class,
				InvalidUUIDTestCase.class,
				IsStoredTestCase.class,
				MessagingTestCase.class,
				MultiDeleteTestCase.class,
				MultiLevelIndexTestCase.class,
				NestedArraysTestCase.class,
				NullWrapperQueriesTestCase.class,
				ObjectSetIDsTestCase.class,
				ParameterizedEvaluationTestCase.class,
				PeekPersistedTestCase.class,
				PersistStaticFieldValuesTestCase.class,
				QueryForUnknownFieldTestCase.class,
				QueryNonExistantTestCase.class,
				ReadCollectionNQTestCase.class,
				ReadCollectionQBETestCase.class,
				ReadCollectionSODATestCase.class,
				ReadObjectNQTestCase.class,
				ReadObjectQBETestCase.class,
				ReadObjectSODATestCase.class,
				RefreshTestCase.class,
				UpdateCollectionTestCase.class,
				UpdateObjectTestCase.class,
		};
	}

}
