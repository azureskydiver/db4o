/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.handlers;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class TypeHandlerConfigurationTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		
		public List _list;
		
		public Item(List list){
			_list = list;
		}
		
	}
	
	public void store(){
		store(new Item(new ArrayList()));
	}
	
	public void test(){
		if(NullableArrayHandling.disabled()){
			return;
		}
		ClassMetadata classMetadata = classMetadata(ArrayList.class);
		assertSingleNullTypeHandlerAspect(classMetadata);
	}

	private void assertSingleNullTypeHandlerAspect(ClassMetadata classMetadata) {
		final IntByRef aspectCount = new IntByRef(0);
		classMetadata.forEachDeclaredAspect(new Procedure4() {
			public void apply(Object arg) {
				aspectCount.value ++;
				Assert.isSmaller(2, aspectCount.value);
				ClassAspect aspect = (ClassAspect) arg;
				Assert.isInstanceOf(TypeHandlerAspect.class, aspect);
				TypeHandlerAspect typeHandlerAspect = (TypeHandlerAspect) aspect;
				Assert.isInstanceOf(IgnoreFieldsTypeHandler.class, typeHandlerAspect._typeHandler);
			}
		});
	}

	private ClassMetadata classMetadata(Class clazz) {
		return container().classMetadataForName(clazz.getName());
	}

}
