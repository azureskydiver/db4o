/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * @exclude
 */
public abstract class ConversionStage {
	
	public final static class ClassCollectionAvailableStage extends ConversionStage {
		
		public ClassCollectionAvailableStage(YapFile file) {
			super(file);
		}

		public void accept(Conversion conversion) {
			conversion.convert(this);
		}
	}

	public final static class SystemUpStage extends ConversionStage {
		public SystemUpStage(YapFile file) {
			super(file);
		}
		public void accept(Conversion conversion) {
			conversion.convert(this);
		}
	}

	private YapFile _file;
	
	protected ConversionStage(YapFile file) {
		_file = file;
	}

	public YapFile file() {
		return _file;
	}
	
    public SystemData systemData(){
        return _file.systemData();
    }
	
	public abstract void accept(Conversion conversion);
}
