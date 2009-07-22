/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.db4ounit.common.refactor;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.refactor.ReAddFieldTestCase.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.util.*;

public class RemovedClassRefactoringTestCase extends AbstractDb4oTestCase{
	
	public static class Super {
		
		public String _superField;
		
		public Super(String super_) {
			_superField = super_;
		}

	}
	
	public static class Sub extends Super {
		
		public String _subField; 
		
		public Sub(String super_, String sub) {
			super(super_);
			_subField = sub;
		}

	}
	
	public static class NoSuper {
		
		public NoSuper(String sub){
			_subField = "foo";
		}
		
		public String _subField;
		
	}
	
	@Override
	protected void store() throws Exception {
		Sub sub = new Sub("super", "sub");
		store(sub);
	}
	
	public void testWithMissingSuperclass() throws Exception{
		assertClassIsUsableAfterRefactoring(new ExcludingReflector(Super.class));
	}

	public void testWithAvailableSuperclass() throws Exception{
		assertClassIsUsableAfterRefactoring(new ExcludingReflector());
	}

	private void assertClassIsUsableAfterRefactoring(Reflector reflector) throws Exception {
		if(true){
			return;
		}
		fixture().resetConfig();
		Configuration config = fixture().config();
		config.reflectWith(reflector);
		TypeAlias alias = new TypeAlias(Sub.class, NoSuper.class);
		config.addAlias(alias);
		reopen();
		
		NoSuper result = retrieveOnlyInstance(NoSuper.class);
		Assert.areEqual("sub", result._subField);
		
		NoSuper newSuper = new NoSuper("foo");
		store(newSuper);
		
		Query q = newQuery(NoSuper.class);
		q.descend("_subField").constrain("foo");
		ObjectSet<NoSuper> objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		result = objectSet.next();
	}

}
