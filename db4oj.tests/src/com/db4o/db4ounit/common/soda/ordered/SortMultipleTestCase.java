package com.db4o.db4ounit.common.soda.ordered;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SortMultipleTestCase extends AbstractDb4oTestCase {

	public static class Data {
		public int _first;
		public int _second;

		public Data(int first, int second) {
			this._first = first;
			this._second = second;
		}

		public boolean equals(Object obj) {
			if(this==obj) {
				return true;
			}
			if(obj==null||getClass()!=obj.getClass()) {
				return false;
			}
			Data data=(Data)obj;
			return _first==data._first&&_second==data._second;
		}
		
		public int hashCode() {
			return 29*_first+_second;
		}
		
		public String toString() {
			return _first+"/"+_second;
		}
	}
	
	private final static Data[] DATA={
		new Data(1,2), // 0
		new Data(1,4), // 1
		new Data(2,4), // 2
		new Data(3,1), // 3
		new Data(4,3), // 4
		new Data(4,1)  // 5
	};
	
	protected void store() throws Exception {
		for (int dataIdx = 0; dataIdx < DATA.length; dataIdx++) {
			store(DATA[dataIdx]);
		}
	}

	public void testSortFirstThenSecond() {
		Query query=newQuery(Data.class);
		query.descend("_first").orderAscending();
		query.descend("_second").orderAscending();
		assertSortOrder(query, new int[]{0,1,2,3,5,4});
	}

	public void testSortSecondThenFirst() {
		Query query=newQuery(Data.class);
		query.descend("_second").orderAscending();
		query.descend("_first").orderAscending();
		assertSortOrder(query, new int[]{3,5,0,4,1,2});
	}

	private void assertSortOrder(Query query, int[] expectedIndexes) {
		ObjectSet result=query.execute();
		Assert.areEqual(expectedIndexes.length,result.size());
		for (int i = 0; i < expectedIndexes.length; i++) {
			Assert.areEqual(DATA[expectedIndexes[i]], result.next());
		}
	}
}
