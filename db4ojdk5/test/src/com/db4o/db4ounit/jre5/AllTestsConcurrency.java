/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5;

import com.db4o.db4ounit.common.concurrency.*;
import com.db4o.db4ounit.jre11.concurrency.*;
import com.db4o.db4ounit.jre5.concurrency.query.*;

import db4ounit.extensions.*;

public class AllTestsConcurrency extends Db4oConcurrenyTestSuite {
	
	public static void main(String[] args) {
		System.exit(new AllTestsConcurrency().runConcurrencyAll());
	}

	protected Class[] testCases() {
		return new Class[] { 
				ArrayNOrderTestCase.class, 
				ByteArrayTestCase.class,
				CascadeDeleteDeletedTestCase.class,
				CascadeDeleteFalseTestCase.class,
				CascadeOnActivateTestCase.class,
				CascadeOnUpdateTestCase.class,
				CascadeOnUpdate2TestCase.class,
				CascadeToExistingVectorMemberTestCase.class,
				CascadeToVectorTestCase.class,
				CaseInsensitiveTestCase.class,
				Circular1TestCase.class,
				Circular2TestCase.class,
				ClientDisconnectTestCase.class,
				ConcurrentQueryTestCase.class,
				CreateIndexInheritedTestCase.class,
				DeepSetTestCase.class,
				DeleteDeepTestCase.class,
				DifferentAccessPathsTestCase.class,
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
				ReadObjectNQTestCase.class,
				ReadObjectQBETestCase.class,
				ReadObjectSODATestCase.class,
				RefreshTestCase.class,
				UpdateObjectTestCase.class,
		};
	}

}
