/* Copyright (C) 2009   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

public interface Environment {

	<T> T provide(Class<T> service);

}
