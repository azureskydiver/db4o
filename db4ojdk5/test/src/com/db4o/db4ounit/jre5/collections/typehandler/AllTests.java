/* Copyright (C) 2006 - 2007 db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
    
    public static void main(String[] arguments) {
        new AllTests().runSolo();
    }
    
    protected Class[] testCases() {
        Class[] classes = 
            new Class[] {
	        ListTypeHandlerCascadedDeleteTestCase.class,
	        ListTypeHandlerPersistedCountTestCase.class,
			ListTypeHandlerTestSuite.class,
			ListTypeHandlerGreaterSmallerTestSuite.class,
			ListTypeHandlerStringElementTestSuite.class,
		};
        return addJavaTestCases(classes);
	}
    
    /**
     * @sharpen.remove null
     */
    protected Class[] javaOnlyTestCases(){
        return new Class[] {
            // Somehow this method does not get removed by sharpen.
            // NamedArrayListTypeHandlerTestCase.class,
        };
    }
    
   protected Class[] addJavaTestCases(Class[] classes){
        Class[] javaTestCases = javaOnlyTestCases(); 
        if(javaTestCases == null){
            return classes;
        }
        int len = javaTestCases.length;
        Class[] allClasses = new Class[classes.length + len];
        System.arraycopy(javaTestCases, 0, allClasses, 0,len );
        System.arraycopy(classes, 0, allClasses, len,classes.length);
        return allClasses;
    }


}
