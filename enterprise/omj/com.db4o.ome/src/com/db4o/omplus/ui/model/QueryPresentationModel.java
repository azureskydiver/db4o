package com.db4o.omplus.ui.model;

import com.db4o.omplus.*;

public class QueryPresentationModel {

	private ErrorMessageSink err;
	
	public QueryPresentationModel(ErrorMessageSink err) {
		this.err = err;
	}
	
	public ErrorMessageSink err() {
		return err;
	}
	
}
