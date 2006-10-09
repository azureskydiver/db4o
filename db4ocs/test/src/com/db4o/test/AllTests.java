/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import db4ounit.extensions.CSTestSuite;
import db4ounit.extensions.Timer;

public class AllTests extends CSTestSuite {

	protected Class[] testCases() {
		return passedTestCases();
		//return failedTestCases();
	}
	
	protected Class[] passedTestCases() {
		return new Class[] {
				ArrayNOrder.class,
				ByteArray.class,
				CascadeOnActivate.class,
				CascadeOnUpdate.class,
				CascadeOnUpdate2.class,
				CascadeToExistingVectorMember.class,
				CascadeToHashtable.class,
				CascadeToVector.class,
				CaseInsensitive.class,
				Circular1.class,
				Circular2.class,
				ComparatorSort.class,
				CreateIndexInherited.class,
				DeepSet.class,
				DifferentAccessPaths.class,
				ExtMethods.class,
				GetAll.class,
				HashtableModifiedUpdateDepth.class,
				IndexCreateDrop.class,
				IndexedByIdentity.class,
				IndexedUpdatesWithNull.class,
				InvalidUUID.class,
				InternStrings.class,
				IsStored.class,
				GreaterOrEqual.class,
				MultiLevelIndex.class,
				MultiDelete.class,
				NestedArrays.class,
				NullWrapperQueries.class,
				ObjectSetIDs.class,
				ParameterizedEvaluation.class,
				PeekPersisted.class,
				PersistStaticFieldValues.class,
				ReadObjectQBETest.class,
				ReadObjectSODATest.class,
				ReadObjectNQTest.class,
				ReadCollectionQBETest.class,
				ReadCollectionSODATest.class,
				ReadCollectionNQTest.class,
				UpdateObjectTest.class,
				UpdateCollectionTest.class,
				};
	}
	
	// failed test cases
	protected Class[] failedTestCases() {
		return new Class[] {
				CascadeDeleteArray.class,
				CascadeDeleteDeleted.class,
				CascadeDeleteFalse.class,
				CascadeOnSet.class,
				ClientDisconnect.class,
				CustomActivationDepth.class,
				DeleteDeep.class,
				DualDelete.class,
				Messaging.class,
		};
	}
	
	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.start();
		new AllTests().run();
		timer.stop();
		System.out.println("Time elapsed: "+ timer.elapsed()+"ms");
	}
}
