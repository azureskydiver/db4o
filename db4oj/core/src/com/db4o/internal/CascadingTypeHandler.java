/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.activation.*;

public interface CascadingTypeHandler extends TypeHandler4{

	void cascadeActivation(Transaction trans, Object obj, ActivationDepth depth);

}