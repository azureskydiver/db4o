package com.db4o.nativequery.optimization.db4o;

import java.util.*;

import org.easymock.*;

import com.db4o.nativequery.expr.cmp.*;
import com.db4o.query.*;

public class QueryMockBuilder {
	private List mocks=new LinkedList();

	public void assertOperator(Constraint constraint, ComparisonOperator op) {
		switch(op.id()) {
			case ComparisonOperator.SMALLER_ID :
				constraint.smaller();
				EasyMock.expectLastCall().andReturn(constraint);
				break;
			case ComparisonOperator.GREATER_ID :
				constraint.greater();
				EasyMock.expectLastCall().andReturn(constraint);
				break;
			default:
				break;
		}
	}

	public Query query() {
		Query query=(Query)EasyMock.createMock(Query.class);
		mocks.add(query);
		return query;
	}
	
	public Query assertDescend(Query query,String fieldName) {
		Query subquery=(Query)EasyMock.createMock(Query.class);
		query.descend(fieldName);
		EasyMock.expectLastCall().andReturn(subquery);
		mocks.add(subquery);
		return subquery;
	}

	public Constraint assertConstrain(Query query,Object value) {
		Constraint constraint=(Constraint)EasyMock.createMock(Constraint.class);
		query.constrain(value);
		EasyMock.expectLastCall().andReturn(constraint);
		mocks.add(constraint);
		return constraint;
	}
	
	public void replay() {
		for (Iterator iter = mocks.iterator(); iter.hasNext();) {
			EasyMock.replay(iter.next());
		}
	}

	public void verify() {
		for (Iterator iter = mocks.iterator(); iter.hasNext();) {
			EasyMock.verify(iter.next());
		}
	}

	public void assertNegated(Constraint constraint,boolean negated) {
		if(negated) {
			constraint.not();
			EasyMock.expectLastCall().andReturn(constraint);
		}
	}
}
