package com.db4o.binding.verifiers;

import java.util.*;

import com.db4o.binding.converters.*;
import com.db4o.binding.verifier.*;

public class DateVerifier extends DateConversionSupport implements IVerifier {
	// TODO: Can we do any sensible (locale-independent) checking here?
	public boolean verifyFragment(String fragment) {
		return true;
	}

	public boolean verifyFullValue(String value) {
		return parse(value)!=null;
	}

	public String getHint() {
		Date sampleDate=new Date();
		StringBuffer samples=new StringBuffer();
		for(int formatterIdx=1;formatterIdx<numFormatters()-2;formatterIdx++) {
			samples.append('\'');
			samples.append(format(sampleDate,formatterIdx));
			samples.append("', ");
		}
        samples.append('\'');
        samples.append(format(sampleDate,0));
        samples.append('\'');
		return "Examples: "+samples+",...";
	}
}
