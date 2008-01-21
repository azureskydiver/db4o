/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.internal.*;
import com.db4o.internal.fieldhandlers.FieldHandlerRefactoring;
import com.db4o.internal.fieldhandlers.UntypedArrayFieldHandler;
import com.db4o.internal.fieldhandlers.FieldHandler;
import com.db4o.internal.fieldhandlers.UntypedMultidimensionalArrayFieldHandler;
import com.db4o.internal.handlers.IntHandler;
import com.db4o.internal.handlers.PlainObjectHandler;
import com.db4o.reflect.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public class HandlerRegistryTestCase extends AbstractDb4oTestCase {
	
	public interface FooInterface {
	}

	public void _testInterfaceHandlerIsSameAsObjectHandler() {
		Assert.areSame(
				handlerForClass(Object.class),
				handlerForClass(FooInterface.class));
	}

	private TypeHandler4 handlerForClass(Class clazz) {
		return handlers().handlerForClass(stream(), reflectClass(clazz));
	}

	private HandlerRegistry handlers() {
		return stream().handlers();
	}
	
	public void testTypeHandlerForID(){
	    assertTypeHandlerClass(Handlers4.INT_ID, IntHandler.class);
	    if(FieldHandlerRefactoring.COMPLETED){
	        assertTypeHandlerClass(Handlers4.UNTYPED_ID, PlainObjectHandler.class);
	    }
	}

    private void assertTypeHandlerClass(int id, Class clazz) {
        TypeHandler4 typeHandler = handlers().typeHandlerForID(id);
        Assert.isInstanceOf(clazz, typeHandler);
    }
	
	public void testTypeHandlerID(){
	    assertTypeHandlerID(Handlers4.INT_ID, integerClassReflector());
	    if(FieldHandlerRefactoring.COMPLETED){
	        assertTypeHandlerID(Handlers4.UNTYPED_ID, objectClassReflector());
	    }
	}

    private void assertTypeHandlerID(int handlerID, ReflectClass integerClassReflector) {
        TypeHandler4 typeHandler = handlers().typeHandlerForClass(integerClassReflector);
	    int id = handlers().typeHandlerID(typeHandler);
        Assert.areEqual(handlerID, id);
    }
	
	public void testTypeHandlerForClass(){
	    Assert.isInstanceOf(
	        IntHandler.class, 
	        handlers().typeHandlerForClass(integerClassReflector()));
	    if(FieldHandlerRefactoring.COMPLETED){
            Assert.isInstanceOf(
                PlainObjectHandler.class, 
                handlers().typeHandlerForClass(objectClassReflector()));
	    }
	}
	
	public void testFieldHandlerForID(){
	    assertFieldHandler(Handlers4.INT_ID, PrimitiveFieldHandler.class);
	    assertFieldHandler(Handlers4.ANY_ARRAY_ID, UntypedArrayFieldHandler.class);
	    assertFieldHandler(Handlers4.ANY_ARRAY_N_ID, UntypedMultidimensionalArrayFieldHandler.class);
	}

    private void assertFieldHandler(int handlerID, Class fieldHandlerClass) {
        FieldHandler fieldHandler = handlers().fieldHandlerForId(handlerID);
        Assert.isInstanceOf(fieldHandlerClass, fieldHandler);
    }
	
	public void testClassForID(){
	    ReflectClass byReflector = integerClassReflector();
	    ReflectClass byID = handlers().classForID(Handlers4.INT_ID);
        Assert.isNotNull(byID);
        Assert.areEqual(byReflector, byID);
	}

	public void testClassReflectorForHandler(){
        ReflectClass byReflector = integerClassReflector();
        ReflectClass byID = handlers().classForID(Handlers4.INT_ID);
        Assert.isNotNull(byID);
        Assert.areEqual(byReflector, byID);
    }
	
    private ReflectClass objectClassReflector() {
        return reflectorFor(Object.class);
    }
	
    private ReflectClass integerClassReflector() {
        return reflectorFor(Integer.class);
    }

    private ReflectClass reflectorFor(Class clazz) {
        return reflector().forClass(clazz);
    }
	
	public static void main(String[] arguments) {
        new HandlerRegistryTestCase().runSolo();
    }
	
}
