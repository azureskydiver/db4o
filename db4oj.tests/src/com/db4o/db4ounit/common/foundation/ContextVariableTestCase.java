/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class ContextVariableTestCase implements TestCase {
	
	public void testSingleThread() {
		final ContextVariable variable = new ContextVariable();
		Assert.isNull(variable.value());
		
		variable.with("foo", new Runnable() {
			public void run() {
				Assert.areEqual("foo", variable.value());
				variable.with("bar", new Runnable() {
					public void run() {
						Assert.areEqual("bar", variable.value());
					}
				});
				Assert.areEqual("foo", variable.value());
			}
		});
		Assert.isNull(variable.value());
	}
	
	public void testMultipleThreads() {
		
	}
	
	public void testTypeChecking() {
		
		final Runnable emptyBlock = new Runnable() {
			public void run() {
			}
		};
		
		final ContextVariable stringVar = new ContextVariable(String.class);
		stringVar.with("foo", emptyBlock);
		
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				stringVar.with(Boolean.TRUE, emptyBlock);
			}
		});
		
	}

}
