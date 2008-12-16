/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class IteratorsTestCase implements TestCase {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(IteratorsTestCase.class).run();
	}
	
	public void testIterateSingle() {
		final Iterator4 i = Iterators.singletonIterator("foo");
		Assert.isTrue(i.moveNext());
		Assert.areEqual("foo", i.current());
		Assert.isFalse(i.moveNext());
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				Assert.isNotNull(i.current());
			}
		});
	}
	
	public void testEnumerate() {
		Iterable4 e = Iterators.enumerate(Iterators.iterable(new Object[] { "1", "2" }));
		
		Iterator4 iterator = e.iterator();
		EnumerateIterator.Tuple first = (EnumerateIterator.Tuple)Iterators.next(iterator);
		EnumerateIterator.Tuple second = (EnumerateIterator.Tuple)Iterators.next(iterator);
		Assert.areEqual(0, first.index);
		Assert.areEqual("1", first.value);
		Assert.areEqual(1, second.index);
		Assert.areEqual("2", second.value);
		
		Assert.isFalse(iterator.moveNext());
	}
	
	public void testCrossProduct() {
		Iterable4[] source = new Iterable4[] {
			iterable(new Object[] { "1", "2" }),
			iterable(new Object[] { "3", "4" }),
			iterable(new Object[] { "5", "6" }),
		};
		String[] expected = {
			"[1, 3, 5]",
			"[1, 3, 6]",
			"[1, 4, 5]",
			"[1, 4, 6]",
			"[2, 3, 5]",
			"[2, 3, 6]",
			"[2, 4, 5]",
			"[2, 4, 6]",
		};
		final Iterator4 iterator = Iterators.crossProduct(source).iterator();
		Iterator4Assert.areEqual(expected, Iterators.map(iterator, new Function4() {
			public Object apply(Object arg) {
				return Iterators.toString((Iterable4)arg);
			}
		}));
	}
	
	private Iterable4 iterable(Object[] objects) {
		return Iterators.iterable(objects);
	}

	public void testFlatten() {
		Iterator4 iterator = iterate(new Object[] {
			"1",
			"2",
			iterate(new Object[] {
				iterate(new Object[] {
					"3",
					"4"
				}),
				Iterators.EMPTY_ITERATOR,
				Iterators.EMPTY_ITERATOR,
				"5"
			}),
			Iterators.EMPTY_ITERATOR,
			"6"
		});
		
		Iterator4Assert.areEqual(
			new Object[] { "1", "2", "3", "4","5", "6" },
			Iterators.flatten(iterator));
	}
	
	Iterator4 iterate(Object[] values) {
		return Iterators.iterate(values);
	}
	
	public void testFilter() {
		assertFilter(
				new String[] { "bar", "baz" },
				new String[] { "foo", "bar", "baz", "zong" },
				new Predicate4() {
					public boolean match(Object candidate) {
						return ((String)candidate).startsWith("b");
					}});
		
		assertFilter(
				new String[] { "foo", "bar" },
				new String[] { "foo", "bar" },
				new Predicate4() {
					public boolean match(Object candidate) {
						return true;
					}
				});
		
		assertFilter(
				new String[0],
				new String[] { "foo", "bar" },
				new Predicate4() {
					public boolean match(Object candidate) {
						return false;
					}
				});
	}

	private void assertFilter(String[] expected, String[] actual, Predicate4 filter) {
		Iterator4Assert.areEqual(expected, Iterators.filter(actual, filter));
	}

	public void testMap() {
		final int[] array = new int[] { 1, 2, 3 };
		final Collection4 args = new Collection4();
		final Iterator4 iterator = Iterators.map(
			IntArrays4.newIterator(array),
			new Function4() {
				public Object apply(Object arg) {
					args.add(arg);
					return new Integer(((Integer)arg).intValue()*2);
				}
			}
		);
		Assert.isNotNull(iterator);
		Assert.areEqual(0, args.size());
		
		for (int i=0; i<array.length; ++i) {
			Assert.isTrue(iterator.moveNext());
			Assert.areEqual(i+1, args.size());
			Assert.areEqual(new Integer(array[i]*2), iterator.current());
		}
	}
	
	public void testEmptyIterator(){
		Iterator4 i = Iterators.EMPTY_ITERATOR;
		Assert.isFalse(i.moveNext());
		i.reset();
	}

}
