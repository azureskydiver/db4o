/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSoloAndClientServer();
	}

	protected Class[] testCases() {
		return new Class[] { 
			ActivationExceptionBubblesUpTestCase.class,
			DatabaseClosedExceptionTestCase.class,
			InvalidPasswordTestCase.class,
			ObjectCanActiviateExceptionTestCase.class,
			ObjectCanDeleteExceptionTestCase.class,
			ObjectOnDeleteExceptionTestCase.class,
			ObjectCanNewExceptionTestCase.class,
			StoreExceptionBubblesUpTestCase.class, 
			StoredClassExceptionBubblesUpTestCase.class,
			TSerializableOnInstantiateCNFExceptionTestCase.class,
			TSerializableOnInstantiateIOExceptionTestCase.class,
			TSerializableOnStoreExceptionTestCase.class,
		};
	}
}
