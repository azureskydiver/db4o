/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.translators;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;

public class TranslatorExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		tryStoreWithoutCallConstructors();
		tryStoreWithCallConstructors();
		storeWithTranslator();
	}

	// end main

	private static void tryStoreWithoutCallConstructors() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.exceptionsOnNotStorable(false);
		configuration.objectClass(NotStorable.class)
				.callConstructor(false);
		tryStoreAndRetrieve(configuration);
	}

	// end tryStoreWithoutCallConstructors

	private static void tryStoreWithCallConstructors() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.exceptionsOnNotStorable(true);
		configuration.objectClass(NotStorable.class)
				.callConstructor(true);
		tryStoreAndRetrieve(configuration);
	}

	// end tryStoreWithCallConstructors

	private static void storeWithTranslator() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(NotStorable.class).translate(
				new NotStorableTranslator());
		tryStoreAndRetrieve(configuration);
	}

	// end storeWithTranslator

	private static void tryStoreAndRetrieve(Configuration configuration) {
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			NotStorable notStorable = new NotStorable(42, "Test");
			System.out.println("ORIGINAL: " + notStorable);
			container.set(notStorable);
		} catch (Exception exc) {
			System.out.println(exc.toString());
			return;
		} finally {
			container.close();
		}
		container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.get(NotStorable.class);
			while (result.hasNext()) {
				NotStorable notStorable = (NotStorable) result.next();
				System.out.println("RETRIEVED: " + notStorable);
				container.delete(notStorable);
			}
		} finally {
			container.close();
		}
	}
	// end tryStoreAndRetrieve
}