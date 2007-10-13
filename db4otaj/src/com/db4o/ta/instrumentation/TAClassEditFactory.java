package com.db4o.ta.instrumentation;

import java.util.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.filter.*;

public class TAClassEditFactory implements ClassEditFactory {

	private final List _prefixes = new ArrayList();

	public void setPrefix(String prefix) {
		_prefixes.add(prefix);
	}
	
	public BloatClassEdit createEdit() {
		ClassFilter filter = null;
		if(_prefixes.isEmpty()) {
			filter = new AcceptAllClassesFilter();
		}
		else {
			filter = new ByNameClassFilter((String[])_prefixes.toArray(new String[_prefixes.size()]), true);
		}
		return new InjectTransparentActivationEdit(filter);
	}

}
