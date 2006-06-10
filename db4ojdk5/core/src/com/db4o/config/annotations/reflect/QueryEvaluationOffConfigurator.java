package com.db4o.config.annotations.reflect;

import com.db4o.config.ObjectField;

public class QueryEvaluationOffConfigurator extends FieldConfigurator {

	public QueryEvaluationOffConfigurator(String className, String fieldName) {
		super(className, fieldName);
	}

	@Override
	protected void configure(ObjectField objectField) {
		objectField.queryEvaluation(false);
	}

}
