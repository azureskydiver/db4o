/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.convert.conversions;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.convert.ConversionStage.*;

/**
 * @exclude
 * @sharpen.partial
 */
public class ReindexNetDateTime_7_8 extends Conversion {
	
	public static final int VERSION = 8;
	
	public void convert(SystemUpStage stage) {
		reindexDateTimeFields(stage);
	}

	private void reindexDateTimeFields(SystemUpStage stage) {
		ClassMetadataIterator i = stage.file().classCollection().iterator();
		while(i.moveNext()){
			ClassMetadata classmetadata = i.currentClass();
			classmetadata.forEachDeclaredField(new Procedure4<FieldMetadata>() {
				public void apply(FieldMetadata field) {
					if(! field.hasIndex()){
						return;
					}
					reindexDateTimeField(field);
				}
			});
		}
	}
	
	/**
	 * @sharpen.ignore
	 */
	private void reindexDateTimeField(FieldMetadata field) {
		// do nothing, code is in partial class in .NET.
	}

}
