/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.internal.*;
import com.db4o.internal.fieldhandlers.*;
import com.db4o.internal.handlers.*;
import com.db4o.reflect.*;

import db4ounit.Assert;
import db4ounit.extensions.*;


public class HandlerRegistryTestCase extends AbstractDb4oTestCase {
	
	public interface FooInterface {
	}
	
	public static class Item{
	    
	}
	
	protected void store() throws Exception {
	    store(new Item());
	}
	
	public void testCorrectHandlerVersion(){
	    UntypedFieldHandler untypedFieldHandler = new UntypedFieldHandler(stream());
	    
        Class untypedFieldHandler2Class =
            MarshallingLogicSimplification.enabled ?
            UntypedFieldHandler2.class : UntypedFieldHandler.class;
        Class arrayHandler2Class = 
            MarshallingLogicSimplification.enabled ?
            ArrayHandler2.class : ArrayHandler.class;
	    
        assertCorrectedHandlerVersion(UntypedFieldHandler0.class, untypedFieldHandler, -1);
        assertCorrectedHandlerVersion(UntypedFieldHandler0.class, untypedFieldHandler, 0);
        assertCorrectedHandlerVersion(untypedFieldHandler2Class, untypedFieldHandler, 1);
        assertCorrectedHandlerVersion(untypedFieldHandler2Class, untypedFieldHandler, 2);
        assertCorrectedHandlerVersion(UntypedFieldHandler.class, untypedFieldHandler, HandlerRegistry.HANDLER_VERSION);
        assertCorrectedHandlerVersion(UntypedFieldHandler.class, untypedFieldHandler, HandlerRegistry.HANDLER_VERSION + 1);
        
        FirstClassObjectHandler firstClassObjectHandler = new FirstClassObjectHandler(itemClassMetadata());
        assertCorrectedHandlerVersion(FirstClassObjectHandler0.class, firstClassObjectHandler, 0);
        assertCorrectedHandlerVersion(FirstClassObjectHandler.class, firstClassObjectHandler, 2);
        
        PrimitiveFieldHandler primitiveFieldHandler = new PrimitiveFieldHandler(null, untypedFieldHandler,0, null );
        assertPrimitiveFieldHandlerDelegate(UntypedFieldHandler0.class, primitiveFieldHandler,0);
        assertPrimitiveFieldHandlerDelegate(untypedFieldHandler2Class, primitiveFieldHandler,1);
        assertPrimitiveFieldHandlerDelegate(untypedFieldHandler2Class, primitiveFieldHandler,2);
        assertPrimitiveFieldHandlerDelegate(UntypedFieldHandler.class, primitiveFieldHandler,HandlerRegistry.HANDLER_VERSION);
        
        ArrayHandler arrayHandler = new ArrayHandler(untypedFieldHandler, false);
        assertCorrectedHandlerVersion(ArrayHandler0.class, arrayHandler, 0);
        assertCorrectedHandlerVersion(arrayHandler2Class, arrayHandler, 1);
        assertCorrectedHandlerVersion(arrayHandler2Class, arrayHandler, 2);
        assertCorrectedHandlerVersion(ArrayHandler.class, arrayHandler, HandlerRegistry.HANDLER_VERSION);
        
        ArrayHandler multidimensionalArrayHandler = new MultidimensionalArrayHandler(untypedFieldHandler, false);
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler0.class, multidimensionalArrayHandler, 0);
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler.class, multidimensionalArrayHandler, 1);
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler.class, multidimensionalArrayHandler, 2);
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler.class, multidimensionalArrayHandler, HandlerRegistry.HANDLER_VERSION);
	    
	}

    private void assertPrimitiveFieldHandlerDelegate(Class fieldHandlerClass,
        PrimitiveFieldHandler primitiveFieldHandler, int version) {
        PrimitiveFieldHandler primitiveFieldHandler0 = (PrimitiveFieldHandler) correctHandlerVersion(primitiveFieldHandler, version);
        Assert.areSame(fieldHandlerClass,primitiveFieldHandler0.delegateTypeHandler().getClass());
    }

    private ClassMetadata itemClassMetadata() {
        return stream().classMetadataForObject(new Item());
    }

    private void assertCorrectedHandlerVersion(Class expectedClass, TypeHandler4 typeHandler, int version) {
        Assert.areSame(expectedClass, correctHandlerVersion(typeHandler, version).getClass());
    }

    private TypeHandler4 correctHandlerVersion(TypeHandler4 typeHandler, int version) {
        return handlers().correctHandlerVersion(typeHandler, version);
    }

	public void testInterfaceHandlerIsSameAsObjectHandler() {
		Assert.areSame(
				handlerForClass(Object.class),
				handlerForClass(FooInterface.class));
	}

	private TypeHandler4 handlerForClass(Class clazz) {
	    return (TypeHandler4) stream().fieldHandlerForClass(reflectClass(clazz));
	}

	private HandlerRegistry handlers() {
		return stream().handlers();
	}
	
	public void testTypeHandlerForID(){
	    assertTypeHandlerClass(Handlers4.INT_ID, IntHandler.class);
	    assertTypeHandlerClass(Handlers4.UNTYPED_ID, PlainObjectHandler.class);
	}

    private void assertTypeHandlerClass(int id, Class clazz) {
        TypeHandler4 typeHandler = handlers().typeHandlerForID(id);
        Assert.isInstanceOf(clazz, typeHandler);
    }
	
	public void testTypeHandlerID(){
	    assertTypeHandlerID(Handlers4.INT_ID, integerClassReflector());
	    assertTypeHandlerID(Handlers4.UNTYPED_ID, objectClassReflector());
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
	    Assert.isInstanceOf(
                PlainObjectHandler.class, 
                handlers().typeHandlerForClass(objectClassReflector()));
	}
	
	public void testFieldHandlerForID(){
	    assertFieldHandler(Handlers4.INT_ID, IntHandler.class);
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
    	if(NullableArrayHandling.disabled()){
    		return reflectorFor(Integer.class);
    	}else{    		
    		return reflectorFor(Platform4.nullableTypeFor(int.class));
    	}
    }

    private ReflectClass reflectorFor(Class clazz) {
        return reflector().forClass(clazz);
    }
	
	public static void main(String[] arguments) {
        new HandlerRegistryTestCase().runSolo();
    }
	
}
