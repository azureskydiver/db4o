/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ids;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class InMemoryIdSystemTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {
	
	private static int MAX_VALID_ID = 1000;
	
	public void testNewIdOverflow(){
		
		LocalObjectContainer container = (LocalObjectContainer) container();
		
		final IdSystem idSystem = new InMemoryIdSystem(container, MAX_VALID_ID);
		
		final List<Integer> allFreeIds = allocateAllAvailableIds(idSystem);
		assertNoMoreIdAvailable(idSystem);
		
		final List<Integer> subSetOfIds = new ArrayList<Integer>();
		
		int counter = 0;
		for (int currentId : allFreeIds){
			counter++;
			if(counter % 3 == 0){
				subSetOfIds.add(currentId);
			}
		}
		
		assertFreeAndReallocate(idSystem, subSetOfIds);
		assertFreeAndReallocate(idSystem, allFreeIds);
		
	}

	private void assertFreeAndReallocate(final IdSystem idSystem,
			final List<Integer> ids) {
		
		// Boundary condition: Last ID. Produced a bug when implementing. 
		if(! ids.contains(MAX_VALID_ID)){
			ids.add(MAX_VALID_ID);
		}
		
		Assert.isGreater(0, ids.size());
		
		idSystem.returnUnusedIds(new Visitable<Integer>() {
			public void accept(Visitor4<Integer> visitor) {
				for(Integer expectedFreeId : ids){
					visitor.visit(expectedFreeId);	
				}
			}
		});
		
		int freedCount = ids.size();
		
		for (int i = 0; i < freedCount; i++) {
			int newId = idSystem.newId();
			Assert.isTrue(ids.contains(newId));
			ids.remove((Object)newId);
		}
		
		Assert.isTrue(ids.size() == 0);
		assertNoMoreIdAvailable(idSystem);
	}

	private List<Integer> allocateAllAvailableIds(final IdSystem idSystem) {
		final List<Integer> ids = new ArrayList<Integer>();
		int newId = 0;
		do{
			newId = idSystem.newId();
			ids.add(newId);
		}
		while(newId < MAX_VALID_ID);
		return ids;
	}

	private void assertNoMoreIdAvailable(final IdSystem idSystem) {
		Assert.expect(Db4oFatalException.class, new CodeBlock() {
			public void run() throws Throwable {
				idSystem.newId();
			}
		});
	}
}
