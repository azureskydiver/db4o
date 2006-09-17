/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.*;
import com.db4o.header.*;

/**
 * @exclude
 */
public abstract class ConversionStage {
	
	public final static class ClassCollectionAvailableStage extends ConversionStage {
		
		public ClassCollectionAvailableStage(YapFile file,FileHeader0 header) {
			super(file,header);
		}

		public void accept(Conversion conversion) {
			conversion.convert(this);
		}
	}

	public final static class SystemUpStage extends ConversionStage {
		public SystemUpStage(YapFile file,FileHeader0 header) {
			super(file,header);
		}
		public void accept(Conversion conversion) {
			conversion.convert(this);
		}
	}

	private YapFile _file;
	private FileHeader0 _header;
	
	protected ConversionStage(YapFile file,FileHeader0 header) {
		_file = file;
		_header=header;
	}

	public YapFile file() {
		return _file;
	}
	
	public FileHeader0 header() {
		return _header;
	}
	
	public abstract void accept(Conversion conversion);
}
