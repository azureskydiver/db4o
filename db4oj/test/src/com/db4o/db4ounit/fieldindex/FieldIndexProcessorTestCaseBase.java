package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.QQueryBase.CreateCandidateCollectionResult;
import com.db4o.db4ounit.btree.ExpectingVisitor;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.*;
import com.db4o.inside.btree.BTree;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

import db4ounit.Assert;

public abstract class FieldIndexProcessorTestCaseBase extends
		FieldIndexTestCaseBase {

	public FieldIndexProcessorTestCaseBase() {
		super();
	}

	protected void configure() {
		super.configure();
		index(ComplexFieldIndexItem.class, "foo");
		index(ComplexFieldIndexItem.class, "bar");
		index(ComplexFieldIndexItem.class, "child");
	}

	protected Query createComplexItemQuery() {
		return createQuery(ComplexFieldIndexItem.class);
	}

	protected IndexedNode selectBestIndex(final Query query) {
		final FieldIndexProcessor processor = createProcessor(query);		
		return processor.selectBestIndex();
	}

	protected FieldIndexProcessor createProcessor(final Query query) {
		final QCandidates candidates = getQCandidates(query);		
		final FieldIndexProcessor processor = new FieldIndexProcessor(candidates);
		return processor;
	}

	private QCandidates getQCandidates(final Query query) {
		final CreateCandidateCollectionResult result = ((QQuery)query).createCandidateCollection();
		QCandidates candidates = (QCandidates)result.candidateCollection._element;
		return candidates;
	}

	protected void assertComplexItemIndex(String expectedFieldIndex, IndexedNode node) {
		Assert.areSame(complexItemIndex(expectedFieldIndex), node.getIndex());
	}

	protected BTree fieldIndexBTree(Class clazz, String fieldName) {
		final ReflectClass reflectClass = stream().reflector().forClass(clazz);
	    return stream().getYapClass(reflectClass, false).getYapField(fieldName).getIndex();
	}

	private BTree complexItemIndex(String fieldName) {
		return fieldIndexBTree(ComplexFieldIndexItem.class, fieldName);
	}

	protected int[] mapToObjectIds(Query itemQuery, int[] foos) {
		int[] lookingFor = clone(foos);
		
		int[] objectIds = new int[foos.length];
		final ObjectSet set = itemQuery.execute();
		while (set.hasNext()) {
			HasFoo item = (HasFoo)set.next();
			for (int i = 0; i < lookingFor.length; i++) {
				if(lookingFor[i] == item.getFoo()){
					lookingFor[i] = -1;
					objectIds[i] = (int) db().getID(item);
					break;
				}
			}
		}		
		
		if (!all(lookingFor, -1)) {
			throw new IllegalArgumentException();
		}
		
		return objectIds;
	}

	private boolean all(int[] array, int value) {
		for (int i=0; i<array.length; ++i) {
			if (value != array[i]) {
				return false;
			}
		}
		return true;
	}

	private int[] clone(int[] bars) {
		int[] array = new int[bars.length];
		System.arraycopy(bars, 0, array, 0, bars.length);
		return array;
	}

	protected void storeComplexItems(int[] foos, int[] bars) {
		ComplexFieldIndexItem last = null;
		for (int i = 0; i < foos.length; i++) {
			last = new ComplexFieldIndexItem(foos[i], bars[i], last);
			store(last);
	    }
	}

	protected void assertTreeInt(final int[] expectedValues, final TreeInt treeInt) {
		final ExpectingVisitor visitor = createExpectingVisitor(expectedValues);
		treeInt.traverse(new Visitor4() {
			public void visit(Object obj) {
				visitor.visit(new Integer(((TreeInt)obj)._key));
			}
		});
		visitor.assertExpectations();
	}

}