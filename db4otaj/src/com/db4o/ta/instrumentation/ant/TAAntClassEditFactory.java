package com.db4o.ta.instrumentation.ant;

import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.util.regexp.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.filter.*;
import com.db4o.ta.instrumentation.*;

public class TAAntClassEditFactory extends ProjectComponent implements ClassEditFactory {

	private final List _regExp = new ArrayList();

	public RegularExpression createRegexp() {
        RegularExpression regExp = new RegularExpression();
        _regExp.add(regExp);
        return regExp;
	}
	
	public BloatClassEdit createEdit() {
		ClassFilter filter = null;
		if(_regExp.isEmpty()) {
			filter = new AcceptAllClassesFilter();
		}
		else {
			Regexp[] regExp = new Regexp[_regExp.size()];
			int idx = 0;
			for (Iterator reIter = _regExp.iterator(); reIter.hasNext();) {
				RegularExpression re = (RegularExpression) reIter.next();
				regExp[idx++] = re.getRegexp(getProject());
			}
			filter = new AntRegExpClassFilter(regExp);
		}
		return new InjectTransparentActivationEdit(filter);
	}

}
