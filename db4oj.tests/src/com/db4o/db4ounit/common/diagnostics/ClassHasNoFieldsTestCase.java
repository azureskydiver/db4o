/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.diagnostics;

import java.util.List;

import com.db4o.config.Configuration;
import com.db4o.diagnostic.ClassHasNoFields;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.foundation.NativeCollections;
import com.db4o.foundation.Predicate4;
import com.db4o.internal.ReflectPlatform;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.CustomClientServerConfiguration;

public class ClassHasNoFieldsTestCase extends AbstractDb4oTestCase implements CustomClientServerConfiguration {
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.diagnostic().addListener(_collector);
	}
	
	public void configureClient(Configuration config) throws Exception {
	}

	public void configureServer(Configuration config) throws Exception {
		configure(config);
	}
	
	public void testDiagnostic() {
		store(new Item());
		
		List<Diagnostic> diagnostics = NativeCollections.filter(
													_collector.diagnostics(),
													new Predicate4<Diagnostic>() {
														public boolean match(Diagnostic candidate) {
															return candidate instanceof ClassHasNoFields;
														}
													});
		Assert.areEqual(1, diagnostics.size());
		
		ClassHasNoFields diagnostic =  (ClassHasNoFields) diagnostics.get(0);
		Assert.areEqual(ReflectPlatform.fullyQualifiedName(Item.class), diagnostic.reason());
	}
	
	private DiagnosticCollector _collector = new DiagnosticCollector();
	
	static public class Item {	
	}
}

