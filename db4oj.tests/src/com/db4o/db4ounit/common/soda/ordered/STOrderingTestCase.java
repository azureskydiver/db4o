package com.db4o.db4ounit.common.soda.ordered;

import java.io.*;
import com.db4o.*;

import com.db4o.db4ounit.common.soda.util.SodaBaseTestCase;
import com.db4o.query.Query;
import db4ounit.extensions.fixtures.OptOutCS;;

/**
 * Tests for COR-1007
 */
public class STOrderingTestCase extends SodaBaseTestCase implements OptOutCS {
	public Object[] createData() {
		return new Object[] {
				/*new OrderTestSubject("Alexandr", 30, 5),   // 0
				new OrderTestSubject("Cris", 30, 5),	   // 1
				new OrderTestSubject("Boris", 30, 5), 	   // 2
				new OrderTestSubject("Helen", 25, 5),	   // 3
				new OrderTestSubject("Zeus", 25, 3),	   // 4
				new OrderTestSubject("Alexsandra", 25, 3), // 5*/
				new OrderTestSubject("Liza", 25, 4),	   // 6
				//new OrderTestSubject("Bred", 25, 3),	   // 7
				new OrderTestSubject("Liza", 25, 3),	   // 6
				new OrderTestSubject("Gregory", 25, 4), }; // 8
	}
	
	/**
	 * Ignored for while due to COR-1007
	 */
	public void _testFirstAndSecondFieldsAreIrrelevant() {
		Query q = newQuery();
		q.constrain(OrderTestSubject.class);
		q.descend("_seniority").orderAscending();
		q.descend("_age").orderAscending();
		q.descend("_name").orderAscending();
		
		expectOrdered(q, new int[] {1, 2, 0} );
	}
	
	/**
	 * Ignored for while due to COR-1007
	 */
	public void _testSecondAndThirdFieldsAreIrrelevant() {
		Query q = newQuery();
		q.constrain(OrderTestSubject.class);
		q.descend("_age").orderAscending();
		q.descend("_name").orderAscending();
		q.descend("_seniority").orderAscending();
		
		expectOrdered(q, new int[] {2, 1, 0} );
	}
	
	public void testOrderByNameAscending() {
		Query q = newQuery();
		q.constrain(OrderTestSubject.class);
		q.descend("_name").orderAscending();
		
		//expectOrdered(q, new int[] {0, 5, 2, 7, 1, 8, 3, 6, 4} );
		expectOrdered(q, new int[] {2, 1, 0} );
	}
	
	public void testOrderByNameAndAgeAscending() {
		Query q = newQuery();
		q.constrain(OrderTestSubject.class);
		q.descend("_seniority").constrain(new Integer(4)).equal(); // just to remove Liza 25 3 from the candidate list
		
		q.descend("_age").orderAscending();
		q.descend("_name").orderAscending();
		
		//expectOrdered(q, new int[] {5, 7, 8, 3, 6, 4, 0, 2, 1} );
		expectOrdered(q, new int[] {2, 0} );
	}
	
	public void testAscendingOrderWithOutAge() {
		Query q = newQuery();
		q.constrain(OrderTestSubject.class);
		q.descend("_seniority").orderAscending();
		q.descend("_name").orderAscending();
	
		//expectOrdered(q, new int[] {5, 4, 7, 8, 6, 0, 2, 1, 3} );
		expectOrdered(q, new int[] {1, 2, 0} );
	}
}
