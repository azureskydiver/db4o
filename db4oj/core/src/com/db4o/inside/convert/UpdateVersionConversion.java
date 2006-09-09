/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.header.*;
import com.db4o.inside.convert.ConversionStage.*;

public class UpdateVersionConversion extends Conversion {

	private final int _version;
	
	public UpdateVersionConversion(int version) {
		_version = version;
	}

	public void convert(SystemUpStage stage) {
		FileHeader0 fileHeader=stage.header();
        fileHeader.converterVersion(_version);
        fileHeader.writeVariablePart1();
	}

}
