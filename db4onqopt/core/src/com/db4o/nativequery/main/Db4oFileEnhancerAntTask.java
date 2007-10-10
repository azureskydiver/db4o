/* Copyright (C) 2007 db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.main;


public class Db4oFileEnhancerAntTask extends com.db4o.instrumentation.ant.Db4oFileEnhancerAntTask {

	public Db4oFileEnhancerAntTask() {
		setEditClass(TranslateNQToSODAEdit.class.getName());
	}
	
}
