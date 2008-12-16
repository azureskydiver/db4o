package com.db4o.foundation;

public interface Environment {

	<T> T provide(Class<T> service);

}
