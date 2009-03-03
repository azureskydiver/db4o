package com.db4o.db4ounit.common.internal;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ClassMetadataTestCase extends AbstractInMemoryDb4oTestCase {
	
	public static class NonStorable {
		
		public NonStorable(int i) {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
	    config.callConstructors(true);
	}
	
	public void testInitialization() {
		
		final ReflectClass reflectClass = reflectClass(NonStorable.class);
		final ClassMetadata subject = new ClassMetadata(container(), reflectClass);
		
		Assert.expect(ObjectNotStorableException.class, new CodeBlock() { public void run() {
			
			subject.createConstructor(reflectClass, reflectClass.getName(), true );
			
		}});
	}

}
