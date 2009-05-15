/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.instrumentation.main;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.file.*;

public interface Db4oInstrumentationListener {
	void notifyProcessed(InstrumentationClassSource source, InstrumentationStatus status);
}
