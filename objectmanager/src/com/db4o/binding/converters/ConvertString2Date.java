package com.db4o.binding.converters;

import com.db4o.binding.converter.*;

public class ConvertString2Date extends DateConversionSupport implements IConverter {
	public Object convert(Object source) {
		return parse(source.toString());
	}	
}
