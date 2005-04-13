package com.db4o.binding.converters;

import java.util.*;

import com.db4o.binding.converter.*;

public class ConvertDate2String extends DateConversionSupport implements IConverter {	
	public Object convert(Object source) {
		return format((Date)source);
	}	
}
