/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;


public class Stack4TestCase implements TestCase {

	public static void main(String[] args) {
		new TestRunner(Stack4TestCase.class).run(); 
	}
	
	public void testPushPop(){
		Stack4 stack = new Stack4();
		Assert.isNull(stack.peek());
		Assert.isNull(stack.pop());
		stack.push("a");
		stack.push("b");
		stack.push("c");
		Assert.areEqual("c", stack.peek());
		Assert.areEqual("c", stack.peek());
		Assert.areEqual("c", stack.pop());
		Assert.areEqual("b", stack.pop());
		Assert.areEqual("a", stack.peek());
		Assert.areEqual("a", stack.pop());
		Assert.isNull(stack.peek());
		Assert.isNull(stack.pop());
	}

}
