package com.db4o.diagnostic;

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
