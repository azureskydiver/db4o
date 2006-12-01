package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.QQueryBase.CreateCandidateCollectionResult;
import com.db4o.config.*;
import com.db4o.db4ounit.common.btree.*;
import com.db4o.db4ounit.common.foundation.IntArrays4;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.btree.BTree;
import com.db4o.inside.classindex.BTreeClassIndexStrategy;
import com.db4o.inside.fieldindex.*;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

import db4ounit.Assert;

public abstract class FieldIndexProcessorTestCaseBase extends
		FieldIndexTestCaseBase {

	public FieldIndexProcessorTestCaseBase() {
		super();
	}

	protected void configure(Configuration config) {
		super.configure(config);
		indexField(config,ComplexFieldIndexItem.class, "foo");
		indexField(config,ComplexFieldIndexItem.class, "bar");
		indexField(config,ComplexFieldIndexItem.class, "child");
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
		return new FieldIndexProcessor(candidates);
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
		return getYapClass(clazz).getYapField(fieldName).getIndex(null);
	}

	private YapClass getYapClass(Class clazz) {
		return stream().getYapClass(getReflectClass(clazz));
	}

	private ReflectClass getReflectClass(Class clazz) {
		return stream().reflector().forClass(clazz);
	}
	
	protected BTree classIndexBTree(Class clazz) {
		return ((BTreeClassIndexStrategy)getYapClass(clazz).index()).btree();
	}

	private BTree complexItemIndex(String fieldName) {
		return fieldIndexBTree(ComplexFieldIndexItem.class, fieldName);
	}

	protected int[] mapToObjectIds(Query itemQuery, int[] foos) {
		int[] lookingFor = IntArrays4.clone(foos);
		
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
		
		int index = indexOfNot(lookingFor, -1);
		if (-1 != index) {
			throw new IllegalArgumentException("Foo '" + lookingFor[index] + "' not found!");
		}
		
		return objectIds;
	}

	public static int indexOfNot(int[] array, int value) {
		for (int i=0; i<array.length; ++i) {
			if (value != array[i]) {
				return i;
			}
		}
		return -1;
	}

	protected void storeComplexItems(int[] foos, int[] bars) {
		ComplexFieldIndexItem last = null;
		for (int i = 0; i < foos.length; i++) {
			last = new ComplexFieldIndexItem(foos[i], bars[i], last);
			store(last);
	    }
	}

	protected void assertTreeInt(final int[] expectedValues, final TreeInt treeInt) {
		final ExpectingVisitor visitor = BTreeAssert.createExpectingVisitor(expectedValues);
		treeInt.traverse(new Visitor4() {
			public void visit(Object obj) {
				visitor.visit(new Integer(((TreeInt)obj)._key));
			}
		});
		visitor.assertExpectations();
	}

}