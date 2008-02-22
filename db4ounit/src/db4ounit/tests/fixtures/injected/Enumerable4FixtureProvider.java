/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures.injected;

import db4ounit.tests.fixtures.framework.*;

public class Enumerable4FixtureProvider implements FixtureProvider {

	public static final Object TOKEN = new Object();

	public Object[] fixtures() {
		
		Object[][] combinations = null; //combine(new ElementFixtureProvider(), new CollectionFixtureProvider());
		Object[] result = new Object[combinations.length];
		
		for (int i = 0; i < combinations.length; i++) {
			Object[] combination = combinations[i];
			Object[] data = (Object[])combination[0];
			Set4 collection = (Set4)combination[1];
			for (int dataIndex=0; dataIndex<data.length; ++dataIndex) {
				collection.add(data[dataIndex]);
			}
			result[i] = collection;
		}
		return result;
		
//		return Iterators4.map(
//			Iterators4.combine(
//				new ElementFixtureProvider(),
//				new CollectionFixtureProvider()
//			), 
//			new Function4() {
//				public Object apply(Object value) {
//					Object[] combination = (Object[]) value;
//					Object[] data = (Object[])combination[0];
//					Set4 collection = (Set4)combination[1];
//					for (int i=0; i<data.length; ++i) {
//						collection.add(data[i]);
//					}
//					return collection;
//				}
//			});
	}
}
