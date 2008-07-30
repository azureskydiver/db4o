/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.soda;

import com.db4o.db4ounit.jre12.soda.collections.*;
import com.db4o.db4ounit.jre12.soda.deepOR.*;
import com.db4o.db4ounit.jre12.soda.experiments.*;
import com.db4o.internal.*;

import db4ounit.extensions.*;

public class AllTests  extends Db4oTestSuite {
	protected Class[] testCases() {
		 
		return merge(new Class[]{
				HashtableModifiedUpdateDepthTestCase.class,
				ObjectSetListAPITestCase.class,
				STArrayListTTestCase.class,
				STArrayListUTestCase.class,
				STHashSetTTestCase.class,
				STHashSetUTestCase.class,
				STHashtableDTestCase.class,
				STHashtableEDTestCase.class,
				STHashtableETTestCase.class,
				STHashtableEUTestCase.class,
				STHashtableTTestCase.class,
				STHashtableUTestCase.class,
				STLinkedListTTestCase.class,
				STLinkedListUTestCase.class,
				STOwnCollectionTTestCase.class,
				STTreeSetTTestCase.class,
				STTreeSetUTestCase.class,
				STVectorTTestCase.class,
				STVectorUTestCase.class,
				STOrContainsTestCase.class,
				STCurrentTestCase.class,
				STIdentityEvaluationTestCase.class,
				STNullOnPathTestCase.class,
		}, vectorQbeTestCases());
	}
	
	private Class[] merge(Class[] arr1, Class[] arr2){
		Class[] res = new Class[arr1.length + arr2.length];
		System.arraycopy(arr1, 0, res, 0, arr1.length);
		System.arraycopy(arr2, 0, res, arr1.length, arr2.length);
		return res;
	}

	public static void main(String[] args) {
		new AllTests().runAll();
	}
	
	private Class[] vectorQbeTestCases () {
		return CollectionTypeHandlerRegistry.enabled() ? 
				new Class[] {
			//  QBE with vector is not expressible as SODA and it
			//  will no longer work with new collection Typehandlers
				} 
		: 
				new Class[] {
						STVectorDTestCase.class,
						STVectorEDTestCase.class,
		};
	}
}
