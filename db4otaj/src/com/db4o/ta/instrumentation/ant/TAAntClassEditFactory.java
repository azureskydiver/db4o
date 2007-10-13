package com.db4o.ta.instrumentation.ant;

import org.apache.tools.ant.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.filter.*;
import com.db4o.ta.instrumentation.*;

public class TAAntClassEditFactory extends ProjectComponent implements ClassEditFactory {

	private String _regExp;

	public void setPattern(String regExp) {
		_regExp = regExp;
	}
	
	public BloatClassEdit createEdit() {
		ClassFilter filter = null;
		if(_regExp == null) {
			filter = new AcceptAllClassesFilter();
		}
		else {
			filter = new AntRegExpClassFilter(_regExp, getProject());
		}
		return new InjectTransparentActivationEdit(filter);
	}

}
