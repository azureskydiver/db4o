/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}
	
	protected Class[] testCases() {
		return new Class [] { SwitchingFilesFromClientTestCase.class } ;
		// return passedTestCases();
		//return failedTestCases();
	}
	
	protected Class[] passedTestCases() {
		return new Class[] {
				ArrayNOrder.class,
				ByteArray.class,
				CascadeOnActivate.class,
				CascadeOnSet.class,
//				CascadeOnUpdate.class,
//				CascadeOnUpdate2.class,
//				CascadeToExistingVectorMember.class,
//				CascadeToHashtable.class,
//				CascadeToVector.class,
//				CaseInsensitive.class,
//				Circular1.class,
//				Circular2.class,
//				ClientDisconnect.class,
//				CloseServerBeforeClient.class,
//				ComparatorSort.class,
//				CreateIndexInherited.class,
//				DeepSet.class,
//				DifferentAccessPaths.class,
//				ExtMethods.class,
//				GetAll.class,
//				HashtableModifiedUpdateDepth.class,
//				IndexCreateDrop.class,
//				IndexedByIdentity.class,
//				IndexedUpdatesWithNull.class,
//				InvalidUUID.class,
//				InternStrings.class,
//				IsStored.class,
//				GreaterOrEqual.class,
//				MultiLevelIndex.class,
//				MultiDelete.class,
//				NestedArrays.class,
//				NullWrapperQueries.class,
//				ObjectSetIDs.class,
//				ParameterizedEvaluation.class,
//				PeekPersisted.class,
//				PersistStaticFieldValues.class,
//				QueryForUnknownField.class,
//				QueryNonExistant.class,
//				ReadObjectQBETest.class,
//				ReadObjectSODATest.class,
//				ReadObjectNQTest.class,
//				ReadCollectionQBETest.class,
//				ReadCollectionSODATest.class,
//				ReadCollectionNQTest.class,
//				Refresh.class,
//				QueryForUnknownField.class,
//				UpdateObjectTest.class,
//				UpdateCollectionTest.class,
				};
	}
	
	// failed test cases
	protected Class[] failedTestCases() {
		return new Class[] {
				CascadeDeleteArray.class,
				CascadeDeleteDeleted.class,
				CascadeDeleteFalse.class,
				CascadeOnSet.class,
				CustomActivationDepth.class,
				DeleteDeep.class,
				DualDelete.class,
				Messaging.class,
				SwitchingFilesFromClientTestCase.class,
		};
	}
	
}
