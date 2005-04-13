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
		for(int formatterIdx=0;formatterIdx<numFormatters();formatterIdx++) {
			if(formatterIdx>0) {
				samples.append(", ");
			}
			if(formatterIdx==numFormatters()-1) {
				samples.append("or ");
			}
			samples.append('\'');
			samples.append(format(sampleDate,formatterIdx));
			samples.append('\'');
		}
		return "Please provide a full date spec, such as "+samples+".";
	}
}
