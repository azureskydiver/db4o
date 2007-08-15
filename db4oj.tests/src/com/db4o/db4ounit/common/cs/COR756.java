package com.db4o.db4ounit.common.cs;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

import db4ounit.Assert;

public class COR756 extends ClientServerTestCaseBase  {
	
	
	public static void main(String[] args) {
		new COR756().runAll();
	}
	
	public class A
	{
	  public boolean GetAllB (B b)
	  {
	    return this == b.a;
	  }
	}

	public class B
	{
	  public A a;
	}
	
	public void _testClientServer(){
		final A a = new A();
		final B b = new B();
		b.a = a;
		ObjectContainer client = client();
	    client.set(b);
	    Assert.areEqual(
	    client.query(new Predicate(){
	    	public boolean appliesTo(Object candidate) {
	    		B b = (B)candidate;
	    		return a.GetAllB(b);
	    	};
	    	public Class extentType() {
	    		return B.class;
	    	};
	    } ).size(), 1);
		
	}

}
