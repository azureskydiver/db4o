package com.db4o.internal;

import com.db4o.foundation.*;

public interface BlockSize {

	void register(Listener<Integer> listener);

	void set(int newValue);

	int value();
}
