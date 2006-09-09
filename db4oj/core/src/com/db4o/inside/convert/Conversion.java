/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.inside.convert.ConversionStage.*;

/**
 * @exclude
 */
public interface Conversion {
	void convert(ClassCollectionAvailableStage stage);
	void convert(SystemUpStage stage);
}
