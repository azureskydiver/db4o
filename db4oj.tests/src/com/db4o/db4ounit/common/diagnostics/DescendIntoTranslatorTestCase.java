/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.diagnostics;

import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectTranslator;
import com.db4o.diagnostic.DescendIntoTranslator;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.foundation.NativeCollections;
import com.db4o.foundation.Predicate4;
import com.db4o.internal.ReflectPlatform;
import com.db4o.query.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.OptOutMultiSession;

public class DescendIntoTranslatorTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {

	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).translate(new TItem());
		config.diagnostic().addListener(_collector);
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item("foo"));
	}
	
	public void testDiagnostic() {
		Query query = newQuery(Item.class);
		query.descend("_name").constrain("foo").startsWith(true);
		query.execute();
		
		List<Diagnostic> diagnostics = NativeCollections.filter(
													_collector.diagnostics(),
													new Predicate4<Diagnostic>() {
														public boolean match(Diagnostic candidate) {
															return candidate instanceof DescendIntoTranslator;
														}
													});
		Assert.areEqual(1, diagnostics.size());
		
		DescendIntoTranslator diagnostic =  (DescendIntoTranslator) diagnostics.get(0);
		Assert.areEqual(ReflectPlatform.fullyQualifiedName(Item.class) + "." + "_name", diagnostic.reason());
	}
	
	public static class Item {	
		public Item(String name) {
			_name = name;
		}

		public String getName() {
			return _name;
		}

		public void setName(String name) {
			_name = name;
		}
		
		private String _name ;
	}

	public static class TItem implements ObjectTranslator {

		public void onActivate(ObjectContainer container, Object applicationObject, Object storedObject) {
			Item item = (Item) applicationObject;
			item.setName((String) storedObject);
		}

		public Object onStore(ObjectContainer container, Object applicationObject) {
			String name = ((Item) applicationObject).getName();
			return name;
		}

		public Class storedClass() {
			return String.class;
		}
	}	
	
	private DiagnosticCollector _collector = new DiagnosticCollector();
}