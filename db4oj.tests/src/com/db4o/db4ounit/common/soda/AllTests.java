/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.soda;

import com.db4o.db4ounit.common.soda.arrays.object.*;
import com.db4o.db4ounit.common.soda.arrays.typed.*;
import com.db4o.db4ounit.common.soda.arrays.untyped.*;
import com.db4o.db4ounit.common.soda.classes.simple.*;
import com.db4o.db4ounit.common.soda.classes.typedhierarchy.*;
import com.db4o.db4ounit.common.soda.classes.untypedhierarchy.*;
import com.db4o.db4ounit.common.soda.joins.typed.*;
import com.db4o.db4ounit.common.soda.joins.untyped.*;
import com.db4o.db4ounit.common.soda.ordered.*;
import com.db4o.db4ounit.common.soda.wrapper.typed.*;
import com.db4o.db4ounit.common.soda.wrapper.untyped.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[]{
				STArrMixedTestCase.class,
				STArrStringOTestCase.class,
				STArrStringONTestCase.class,
				STArrStringTTestCase.class,
				STArrStringTNTestCase.class,
				STArrStringUTestCase.class,
				STArrStringUNTestCase.class,
				STArrIntegerOTestCase.class,
				STArrIntegerONTestCase.class,
				STArrIntegerTTestCase.class,
				STArrIntegerTNTestCase.class,
				STArrIntegerUTestCase.class,
				STArrIntegerUNTestCase.class,
				STArrIntegerWTTestCase.class,
				STArrIntegerWTONTestCase.class,
				STArrIntegerWUONTestCase.class,
				STBooleanTestCase.class,
				STBooleanWTTestCase.class,
				STBooleanWUTestCase.class,
				STByteTestCase.class,
				STByteWTTestCase.class,
				STByteWUTestCase.class,
				STCharTestCase.class,
				STCharWTTestCase.class,
				STCharWUTestCase.class,
				STDateTestCase.class,
				STDateUTestCase.class,
				STDoubleTestCase.class,
				STDoubleWTTestCase.class,
				STDoubleWUTestCase.class,
				STETH1TestCase.class,
				STFloatTestCase.class,
				STFloatWTTestCase.class,
				STFloatWUTestCase.class,
				STIntegerTestCase.class,
				STIntegerWTTestCase.class,
				STIntegerWUTestCase.class,
				STLongTestCase.class,
				STLongWTTestCase.class,
				STLongWUTestCase.class,
				STOrTTestCase.class,
				STOrUTestCase.class,
				STOStringTestCase.class,
				STOIntegerTestCase.class,
				STOIntegerWTTestCase.class,
				STRTH1TestCase.class,
				STSDFT1TestCase.class,
				STShortTestCase.class,
				STShortWTTestCase.class,
				STShortWUTestCase.class,
				STStringUTestCase.class,
				STRUH1TestCase.class,
				STTH1TestCase.class,
				STUH1TestCase.class,
		};
	}
	
	public static void main(String[] args) {
		new TestRunner(new Db4oTestSuiteBuilder(new Db4oSolo(),AllTests.class)).run();
	}

}
