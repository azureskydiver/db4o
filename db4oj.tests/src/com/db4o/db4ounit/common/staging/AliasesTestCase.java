/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.staging;

import com.db4o.config.*;
import com.db4o.db4ounit.util.Strings;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 * @exclude
 */
public class AliasesTestCase extends AbstractDb4oTestCase {
	
	
	public static class AFoo{
		
		public String foo;
		
	}
	
	public static class ABar extends AFoo {
		
		public String bar;
		
	}
	
	public static class BFoo {
		
		public String foo;
		
	}
	
	public static class BBar extends BFoo {
		
		public String bar;
		
	}
	

	public static void main(String[] args) {
		new AliasesTestCase().runSolo();
	}
	
	protected void store(){
		ABar bar = new ABar();
		bar.foo = "foo";
		bar.bar = "bar";
		store(bar);
	}
	
	private WildcardAlias createAlias(){
		String className = reflector().forObject(new ABar()).getName();
		String storedPattern = Strings.replace(className, "ABar", "A*");
		String runtimePattern = Strings.replace(className, "ABar", "B*");
		return new WildcardAlias(storedPattern, runtimePattern);
	}
	
	public void test() throws Exception{
		db().configure().addAlias(createAlias());
		reopen();
		debug();
		
		BBar bar = (BBar) retrieveOnlyInstance(BBar.class);
		Assert.areEqual("foo", bar.foo);
		Assert.areEqual("bar", bar.bar);
	}
	
	private void debug(){
		System.out.println(stream().classCollection().toString());
	}

}
