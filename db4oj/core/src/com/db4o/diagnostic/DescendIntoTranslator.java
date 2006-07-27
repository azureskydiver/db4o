/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.diagnostic;

/**
 * Query tries to descend into a field of a class that is configured to be translated
 * (and thus cannot be descended into).
 */
public class DescendIntoTranslator extends DiagnosticBase {
	private String className;
	private String fieldName;
	
	public DescendIntoTranslator(String className, String fieldName) {
		this.className = className;
		this.fieldName = fieldName;
	}

	public String problem() {
		return "Query descends into field(s) of translated class.";
	}

	public Object reason() {
		return className+"."+fieldName;
	}

	public String solution() {
		return "Consider dropping the translator configuration or resort to evaluations/unoptimized NQs.";
	}
}
