package com.db4o.ta.instrumentation.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.util.regexp.*;

import com.db4o.instrumentation.core.*;

public class AntRegExpClassFilter implements ClassFilter {
	private final Regexp _regExp;

	public AntRegExpClassFilter(String exp, Project project) {
        RegularExpression regExp = new RegularExpression();
        regExp.setPattern(exp);
        _regExp = regExp.getRegexp(project);
	}

	public boolean accept(Class clazz) {
		return _regExp.matches(clazz.getName());
	}
}
