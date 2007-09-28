/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.instrumentation;

import EDU.purdue.cs.bloat.editor.*;

public interface BloatClassEdit {
	boolean bloat(ClassEditor ce, ClassLoader origLoader);
}
